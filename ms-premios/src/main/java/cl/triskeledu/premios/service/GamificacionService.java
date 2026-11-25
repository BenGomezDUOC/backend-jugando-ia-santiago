
package cl.triskeledu.premios.service;

import cl.triskeledu.premios.client.ClientesClient;
import cl.triskeledu.premios.client.LocalizacionesClient;

import cl.triskeledu.premios.dto.CapsulaResponse;
import cl.triskeledu.premios.dto.EscanearQrRequest;
import cl.triskeledu.premios.dto.OperacionPuntosResponse;
import cl.triskeledu.premios.dto.UsuarioResponse;
import cl.triskeledu.premios.dto.VisitaSelfieRequest;

import cl.triskeledu.premios.entity.DescubrimientoQr;
import cl.triskeledu.premios.entity.VideoAdicionalVisto;
import cl.triskeledu.premios.entity.VideoVisto;
import cl.triskeledu.premios.entity.VisitaRealizada;

import cl.triskeledu.premios.repository.DescubrimientoQrRepository;
import cl.triskeledu.premios.repository.VideoAdicionalVistoRepository;
import cl.triskeledu.premios.repository.VideoVistoRepository;
import cl.triskeledu.premios.repository.VisitaRealizadaRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GamificacionService {

    private final ClientesClient clientesClient;
    private final LocalizacionesClient localizacionesClient;

    private final DescubrimientoQrRepository
            descubrimientoQrRepository;

    private final VisitaRealizadaRepository
            visitaRealizadaRepository;

    private final VideoVistoRepository videoVistoRepository;

    private final VideoAdicionalVistoRepository
            videoAdicionalVistoRepository;

    private final NotificacionService notificacionService;
    private final SelfieStorageService selfieStorageService;

    private static final int PUNTOS_QR = 10;
    private static final int PUNTOS_SELFIE = 100;

    // --------------------------------------------------
    // ESCANEAR QR (SOBRECARGA PARA COMPATIBILIDAD)
    // --------------------------------------------------

    @Transactional
    public Map<String, Object> escanearQr(EscanearQrRequest request) {
        if (request == null || request.getUsuarioId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El identificador del usuario es obligatorio");
        }
        if (request.getCodigoQr() == null || request.getCodigoQr().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El código QR es obligatorio");
        }
        return escanearQr(request.getUsuarioId(), "", request);
    }

    // --------------------------------------------------
    // ESCANEAR QR
    // --------------------------------------------------

    @Transactional
    public Map<String, Object> escanearQr(
            Long usuarioId,
            String token,
            EscanearQrRequest request
    ) {

        UsuarioResponse usuario = obtenerCliente(
                usuarioId,
                token
        );

        CapsulaResponse capsula =
                localizacionesClient.validarQr(
                        request.getCodigoQr(),
                        token
                );

        if (request.getPuntoIdSeleccionado() != null
                && !request.getPuntoIdSeleccionado().equals(
                capsula.getPuntoId()
        )) {

            throw new IllegalArgumentException(
                    "El código QR no pertenece al punto turístico seleccionado"
            );
        }

        boolean yaDescubierto =
                descubrimientoQrRepository
                        .existsByUsuarioIdAndCapsulaId(
                                usuarioId,
                                capsula.getId()
                        );

        int puntosOtorgados = 0;
        int totalPuntos = usuario.getPuntosAcumulados();

        String mensaje;

        if (!yaDescubierto) {

            DescubrimientoQr descubrimiento =
                    DescubrimientoQr.builder()
                            .usuarioId(usuarioId)
                            .capsulaId(capsula.getId())
                            .fechaTimestamp(
                                    System.currentTimeMillis()
                            )
                            .build();

            descubrimientoQrRepository.saveAndFlush(
                    descubrimiento
            );

            String codigoOperacion =
                    "QR-" + usuarioId + "-" + capsula.getId();

            OperacionPuntosResponse operacion =
                    clientesClient.registrarPuntos(
                            usuarioId,
                            codigoOperacion,
                            PUNTOS_QR,
                            "QR"
                    );

            puntosOtorgados = operacion.isYaProcesada()
                    ? 0
                    : PUNTOS_QR;

            totalPuntos = operacion.getSaldoActual();

            mensaje = operacion.isYaProcesada()
                    ? "Los puntos de este QR ya habían sido otorgados."
                    : "¡QR descubierto exitosamente! Has ganado 10 puntos.";

            notificacionService.crearNotificacion(
                    usuarioId,
                    "¡Nuevo QR descubierto!",
                    "Descubriste la cápsula: "
                            + capsula.getTitulo(),
                    "QR",
                    "coleccion"
            );

        } else {

            mensaje = "Cápsula ya descubierta anteriormente. "
                    + "No suma puntos adicionales.";
        }

        Map<String, Object> response = new HashMap<>();

        response.put("capsula", capsula);
        response.put("puntosGanados", puntosOtorgados);
        response.put("totalPuntosUsuario", totalPuntos);
        response.put("mensaje", mensaje);
        response.put("nuevoDescubrimiento", !yaDescubierto);

        return response;
    }

    // --------------------------------------------------
    // REGISTRAR SELFIE CON FOTOGRAFÍA
    // --------------------------------------------------

    @Transactional
    public Map<String, Object> registrarVisitaSelfie(
            Long usuarioId,
            String token,
            VisitaSelfieRequest request,
            MultipartFile archivo
    ) {

        UsuarioResponse usuario = obtenerCliente(
                usuarioId,
                token
        );

        if (request.getCapsulaId() == null
                || request.getCapsulaId() <= 0) {

            throw new IllegalArgumentException(
                    "La cápsula educativa es obligatoria"
            );
        }

        if (request.getPuntoId() == null
                || request.getPuntoId() <= 0) {

            throw new IllegalArgumentException(
                    "El punto turístico es obligatorio"
            );
        }

        if (request.getCodigoQrEsperado() == null
                || request.getCodigoQrEsperado().isBlank()) {

            throw new IllegalArgumentException(
                    "El código QR es obligatorio"
            );
        }

        // Verificar el QR real contra ms-localizaciones.
        CapsulaResponse capsula =
                localizacionesClient.validarQr(
                        request.getCodigoQrEsperado(),
                        token
                );

        if (!request.getCapsulaId().equals(capsula.getId())) {

            throw new IllegalArgumentException(
                    "El QR no corresponde a la cápsula indicada"
            );
        }

        if (!request.getPuntoId().equals(capsula.getPuntoId())) {

            throw new IllegalArgumentException(
                    "La cápsula no pertenece al punto turístico indicado"
            );
        }

        // La selfie exige haber descubierto previamente el QR.
        boolean qrDescubierto =
                descubrimientoQrRepository
                        .existsByUsuarioIdAndCapsulaId(
                                usuarioId,
                                capsula.getId()
                        );

        if (!qrDescubierto) {

            throw new IllegalArgumentException(
                    "Primero debes descubrir el QR de esta cápsula"
            );
        }

        // Evitar que la misma cápsula otorgue otros 100 puntos.
        Optional<VisitaRealizada> visitaExistente =
                visitaRealizadaRepository
                        .findByUsuarioIdAndCapsulaId(
                                usuarioId,
                                capsula.getId()
                        );

        if (visitaExistente.isPresent()) {

            return respuestaSelfie(
                    visitaExistente.get(),
                    0,
                    usuario.getPuntosAcumulados(),
                    "Esta cápsula ya tiene una selfie registrada",
                    false
            );
        }

        // Guardar y validar una imagen real en el servidor.
        String rutaSelfie = selfieStorageService.guardar(
                archivo,
                usuarioId
        );

        // Si falla la transacción local, eliminar el archivo.
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCompletion(int estado) {

                        if (estado != STATUS_COMMITTED) {
                            selfieStorageService.eliminar(rutaSelfie);
                        }
                    }
                }
        );

        // La ruta siempre la genera nuestro servidor.
        VisitaRealizada visita =
                VisitaRealizada.builder()
                        .usuarioId(usuarioId)
                        .puntoId(capsula.getPuntoId())
                        .capsulaId(capsula.getId())
                        .rutaFotoSelfie(rutaSelfie)
                        .puntosGanados(PUNTOS_SELFIE)
                        .fechaTimestamp(
                                System.currentTimeMillis()
                        )
                        .completada(true)
                        .build();

        visitaRealizadaRepository.saveAndFlush(visita);

        // Guardar la notificación en la transacción local.
        notificacionService.crearNotificacion(
                usuarioId,
                "¡Selfie registrada!",
                "Completaste la evidencia de la cápsula: "
                        + capsula.getTitulo(),
                "EVIDENCIA",
                "historial_actividades"
        );

        // Operación única por cliente y cápsula.
        String codigoOperacion =
                "SELFIE-" + usuarioId + "-" + capsula.getId();

        OperacionPuntosResponse operacion =
                clientesClient.registrarPuntos(
                        usuarioId,
                        codigoOperacion,
                        PUNTOS_SELFIE,
                        "SELFIE"
                );

        int puntosOtorgados = operacion.isYaProcesada()
                ? 0
                : PUNTOS_SELFIE;

        String mensaje = operacion.isYaProcesada()
                ? "La bonificación de esta selfie ya había sido otorgada."
                : "¡Selfie registrada! Ganaste 100 puntos.";

        return respuestaSelfie(
                visita,
                puntosOtorgados,
                operacion.getSaldoActual(),
                mensaje,
                true
        );
    }

    // --------------------------------------------------
    // RECUPERAR SELFIE DEL PROPIO CLIENTE
    // --------------------------------------------------

    @Transactional(readOnly = true)
    public Resource obtenerSelfie(
            Long usuarioId,
            String nombre
    ) {

        validarId(usuarioId);

        String referencia =
                "/api/premios/gamificacion/selfies/" + nombre;

        boolean perteneceAlCliente =
                visitaRealizadaRepository
                        .existsByUsuarioIdAndRutaFotoSelfie(
                                usuarioId,
                                referencia
                        );

        if (!perteneceAlCliente) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Fotografía no disponible"
            );
        }

        return selfieStorageService.obtener(nombre);
    }

    // --------------------------------------------------
    // REGISTRAR VIDEO DE CÁPSULA VISTO
    // --------------------------------------------------

    @Transactional
    public void registrarVideoVisto(
            Long usuarioId,
            Long capsulaId
    ) {

        validarId(usuarioId);
        validarId(capsulaId);

        if (!descubrimientoQrRepository
                .existsByUsuarioIdAndCapsulaId(
                        usuarioId,
                        capsulaId
                )) {

            throw new IllegalArgumentException(
                    "Primero debes descubrir esta cápsula educativa"
            );
        }

        if (!videoVistoRepository
                .existsByUsuarioIdAndCapsulaId(
                        usuarioId,
                        capsulaId
                )) {

            VideoVisto videoVisto = VideoVisto.builder()
                    .usuarioId(usuarioId)
                    .capsulaId(capsulaId)
                    .fechaTimestamp(System.currentTimeMillis())
                    .build();

            videoVistoRepository.saveAndFlush(videoVisto);

            notificacionService.crearNotificacion(
                    usuarioId,
                    "¡Cápsula de IA completada!",
                    "Has completado la visualización de la cápsula educativa.",
                    "VIDEO",
                    "coleccion"
            );
        }
    }

    // --------------------------------------------------
    // REGISTRAR VIDEO ADICIONAL VISTO
    // --------------------------------------------------

    @Transactional
    public void registrarVideoAdicionalVisto(
            Long usuarioId,
            Integer videoAdicionalId
    ) {

        validarId(usuarioId);

        if (videoAdicionalId == null
                || videoAdicionalId <= 0) {

            throw new IllegalArgumentException(
                    "El identificador del video no es válido"
            );
        }

        if (!videoAdicionalVistoRepository
                .existsByUsuarioIdAndVideoAdicionalId(
                        usuarioId,
                        videoAdicionalId
                )) {

            VideoAdicionalVisto video =
                    VideoAdicionalVisto.builder()
                            .usuarioId(usuarioId)
                            .videoAdicionalId(videoAdicionalId)
                            .fechaTimestamp(
                                    System.currentTimeMillis()
                            )
                            .build();

            videoAdicionalVistoRepository.saveAndFlush(video);
        }
    }

    // --------------------------------------------------
    // CONSULTAR DESCUBRIMIENTOS
    // --------------------------------------------------

    @Transactional(readOnly = true)
    public List<DescubrimientoQr>
    obtenerDescubrimientosPorUsuario(Long usuarioId) {

        validarId(usuarioId);

        return descubrimientoQrRepository
                .findByUsuarioId(usuarioId);
    }

    // --------------------------------------------------
    // CONSULTAR VISITAS
    // --------------------------------------------------

    @Transactional(readOnly = true)
    public List<VisitaRealizada>
    obtenerVisitasPorUsuario(Long usuarioId) {

        validarId(usuarioId);

        return visitaRealizadaRepository
                .findByUsuarioId(usuarioId);
    }

    // --------------------------------------------------
    // VALIDAR CLIENTE
    // --------------------------------------------------

    private UsuarioResponse obtenerCliente(
            Long usuarioId,
            String token
    ) {

        validarId(usuarioId);

        UsuarioResponse usuario =
                clientesClient.obtenerUsuario(
                        usuarioId,
                        token
                );

        if (!"CLIENTE".equals(usuario.getRol())) {

            throw new IllegalArgumentException(
                    "Solo los clientes pueden acumular puntos"
            );
        }

        return usuario;
    }

    private void validarId(Long id) {

        if (id == null || id <= 0) {

            throw new IllegalArgumentException(
                    "El identificador no es válido"
            );
        }
    }

    // --------------------------------------------------
    // RESPUESTA DE SELFIE
    // --------------------------------------------------

    private Map<String, Object> respuestaSelfie(
            VisitaRealizada visita,
            int puntosGanados,
            int totalPuntos,
            String mensaje,
            boolean nuevaVisita
    ) {

        Map<String, Object> response = new HashMap<>();

        response.put("visita", visita);
        response.put("puntosGanados", puntosGanados);
        response.put("totalPuntosUsuario", totalPuntos);
        response.put("mensaje", mensaje);
        response.put("nuevaVisita", nuevaVisita);

        return response;
    }
}
