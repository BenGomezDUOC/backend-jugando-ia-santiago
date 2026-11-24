package cl.triskeledu.premios.config;

import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler
        extends ResponseEntityExceptionHandler {

    // Errores de validación de los DTO.
    @Override
    protected ResponseEntity<Object>
    handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {

        Map<String, String> errores =
                new LinkedHashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errores.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        Map<String, Object> respuesta =
                new LinkedHashMap<>();

        respuesta.put("timestamp", LocalDateTime.now());
        respuesta.put("status", 400);
        respuesta.put("mensaje", "Error de validación");
        respuesta.put("detalles", errores);

        return ResponseEntity.badRequest().body(respuesta);
    }

    // Errores de las reglas de negocio.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>>
    handleIllegalArgument(IllegalArgumentException ex) {

        return crearRespuesta(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    // Conflictos de base de datos.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>>
    handleDataIntegrity(DataIntegrityViolationException ex) {

        return crearRespuesta(
                HttpStatus.CONFLICT,
                "La operación entra en conflicto con un registro existente"
        );
    }

    // Fallos de comunicación entre microservicios.
    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<Map<String, Object>>
    handleRemoteService(RestClientException ex) {

        log.warn(
                "Error de comunicación entre microservicios: {}",
                ex.getClass().getSimpleName()
        );

        return crearRespuesta(
                HttpStatus.BAD_GATEWAY,
                "No fue posible completar la comunicación con otro servicio"
        );
    }

    // Conservar los códigos de errores HTTP.
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            Object body,
            HttpHeaders headers,
            HttpStatusCode statusCode,
            WebRequest request
    ) {

        if (ex instanceof ResponseStatusException error) {

            Map<String, Object> respuesta =
                    new LinkedHashMap<>();

            respuesta.put("timestamp", LocalDateTime.now());
            respuesta.put("status", statusCode.value());
            respuesta.put(
                    "mensaje",
                    error.getReason() != null
                            ? error.getReason()
                            : "Solicitud rechazada"
            );

            return super.handleExceptionInternal(
                    ex,
                    respuesta,
                    headers,
                    statusCode,
                    request
            );
        }

        return super.handleExceptionInternal(
                ex,
                body,
                headers,
                statusCode,
                request
        );
    }

    // Errores inesperados.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>>
    handleAllExceptions(Exception ex) {

        log.error(
                "Error interno en ms-premios: {}",
                ex.getClass().getSimpleName()
        );

        return crearRespuesta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error inesperado en el servidor"
        );
    }

    private ResponseEntity<Map<String, Object>>
    crearRespuesta(
            HttpStatus estado,
            String mensaje
    ) {

        Map<String, Object> respuesta =
                new LinkedHashMap<>();

        respuesta.put("timestamp", LocalDateTime.now());
        respuesta.put("status", estado.value());
        respuesta.put("error", estado.getReasonPhrase());
        respuesta.put("mensaje", mensaje);

        return ResponseEntity
                .status(estado)
                .body(respuesta);
    }
}