package cl.triskeledu.clientes.service;

import cl.triskeledu.clientes.dto.OperacionPuntosRequest;
import cl.triskeledu.clientes.dto.OperacionPuntosResponse;
import cl.triskeledu.clientes.entity.OperacionPuntos;
import cl.triskeledu.clientes.entity.Usuario;
import cl.triskeledu.clientes.model.Rol;
import cl.triskeledu.clientes.repository.OperacionPuntosRepository;
import cl.triskeledu.clientes.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OperacionPuntosService {

    private final UsuarioRepository usuarioRepository;

    private final OperacionPuntosRepository
            operacionPuntosRepository;

    // --------------------------------------------------
    // PROCESAR OPERACIÓN DE PUNTOS
    // --------------------------------------------------

    @Transactional
    public OperacionPuntosResponse procesarOperacion(
            Long usuarioId,
            OperacionPuntosRequest request
    ) {

        // Validar identificador del usuario.
        if (usuarioId == null || usuarioId <= 0) {

            throw new IllegalArgumentException(
                    "El identificador del usuario no es válido"
            );
        }

        // Validar los datos de la operación.
        validarSolicitud(request);

        // Bloquear al usuario durante la operación.
        // Evita modificaciones simultáneas de sus puntos.
        Usuario usuario = usuarioRepository
                .findByIdForUpdate(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Usuario no encontrado"
                ));

        // Solo los clientes participan en la gamificación.
        if (usuario.getRol() != Rol.CLIENTE) {

            throw new IllegalArgumentException(
                    "Solo los clientes pueden acumular puntos"
            );
        }

        int saldoActual = usuario.getPuntosAcumulados() != null
                ? usuario.getPuntosAcumulados()
                : 0;

        // --------------------------------------------------
        // COMPROBAR OPERACIÓN DUPLICADA
        // --------------------------------------------------

        Optional<OperacionPuntos> operacionExistente =
                operacionPuntosRepository.findByCodigoOperacion(
                        request.getCodigoOperacion()
                );

        if (operacionExistente.isPresent()) {

            OperacionPuntos existente =
                    operacionExistente.get();

            // Comprobar que corresponde a la misma solicitud.
            validarOperacionExistente(
                    existente,
                    usuarioId,
                    request
            );

            // No acumular nuevamente los puntos.
            return construirRespuesta(
                    existente,
                    saldoActual,
                    true
            );
        }

        // --------------------------------------------------
        // CALCULAR NUEVO SALDO
        // --------------------------------------------------

        long nuevoSaldo =
                (long) saldoActual + request.getPuntos();

        // Impedir saldos negativos.
        if (saldoActual < 0 || nuevoSaldo < 0) {

            throw new IllegalArgumentException(
                    "El saldo de puntos del usuario no es válido"
            );
        }

        // Evitar desbordamientos numéricos.
        if (nuevoSaldo > Integer.MAX_VALUE) {

            throw new IllegalArgumentException(
                    "Se superó el límite permitido de puntos"
            );
        }

        // --------------------------------------------------
        // REGISTRAR MOVIMIENTO
        // --------------------------------------------------

        OperacionPuntos operacion = OperacionPuntos.builder()
                .codigoOperacion(request.getCodigoOperacion())
                .usuario(usuario)
                .puntos(request.getPuntos())
                .motivo(request.getMotivo())
                .saldoAnterior(saldoActual)
                .saldoResultante((int) nuevoSaldo)
                .fechaOperacion(LocalDateTime.now())
                .build();

        // Guardar el movimiento y comprobar sus restricciones.
        OperacionPuntos guardada =
                operacionPuntosRepository.saveAndFlush(operacion);

        // Actualizar los puntos acumulados del usuario.
        usuario.setPuntosAcumulados((int) nuevoSaldo);

        usuarioRepository.save(usuario);

        // Devolver el resultado de la operación.
        return construirRespuesta(
                guardada,
                (int) nuevoSaldo,
                false
        );
    }

    // --------------------------------------------------
    // VALIDAR SOLICITUD
    // --------------------------------------------------

    private void validarSolicitud(
            OperacionPuntosRequest request
    ) {

        if (request == null) {

            throw new IllegalArgumentException(
                    "La solicitud es obligatoria"
            );
        }

        String codigo = request.getCodigoOperacion();

        if (codigo == null
                || codigo.isBlank()
                || codigo.length() > 150
                || !codigo.matches("^[A-Z0-9_-]+$")) {

            throw new IllegalArgumentException(
                    "El código de operación no es válido"
            );
        }

        // Solo permite QR de 10 puntos y selfies de 100 puntos.
        if (!request.isOperacionValida()) {

            throw new IllegalArgumentException(
                    "Los puntos no corresponden al motivo de la operación"
            );
        }

        // Verificar que el código corresponda al motivo.
        if (!codigo.startsWith(request.getMotivo() + "-")
                || codigo.length() <= request.getMotivo().length() + 1) {

            throw new IllegalArgumentException(
                    "El código no corresponde al motivo de la operación"
            );
        }
    }

    // --------------------------------------------------
    // VALIDAR OPERACIÓN EXISTENTE
    // --------------------------------------------------

    private void validarOperacionExistente(
            OperacionPuntos existente,
            Long usuarioId,
            OperacionPuntosRequest request
    ) {

        boolean mismoUsuario =
                existente.getUsuario().getId().equals(usuarioId);

        boolean mismosPuntos =
                existente.getPuntos().equals(request.getPuntos());

        boolean mismoMotivo =
                existente.getMotivo().equals(request.getMotivo());

        // Impedir reutilizar un código con datos diferentes.
        if (!mismoUsuario || !mismosPuntos || !mismoMotivo) {

            throw new IllegalArgumentException(
                    "El código ya pertenece a otra operación"
            );
        }
    }

    // --------------------------------------------------
    // CONSTRUIR RESPUESTA
    // --------------------------------------------------

    private OperacionPuntosResponse construirRespuesta(
            OperacionPuntos operacion,
            int saldoActual,
            boolean yaProcesada
    ) {

        String fecha = operacion.getFechaOperacion()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        return OperacionPuntosResponse.builder()
                .idOperacion(operacion.getId())
                .codigoOperacion(operacion.getCodigoOperacion())
                .usuarioId(operacion.getUsuario().getId())
                .puntos(operacion.getPuntos())
                .motivo(operacion.getMotivo())
                .saldoAnterior(operacion.getSaldoAnterior())
                .saldoResultante(operacion.getSaldoResultante())
                .saldoActual(saldoActual)
                .yaProcesada(yaProcesada)
                .fechaOperacion(fecha)
                .mensaje(
                        yaProcesada
                                ? "La operación ya fue procesada"
                                : "Operación procesada correctamente"
                )
                .build();
    }
}