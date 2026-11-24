package cl.triskeledu.clientes.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler
        extends ResponseEntityExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // --------------------------------------------------
    // ERRORES DE VALIDACIÓN DE LOS DTO

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {

        Map<String, String> errores = new LinkedHashMap<>();

        for (FieldError error :
                ex.getBindingResult().getFieldErrors()) {

            errores.putIfAbsent(
                    error.getField(),
                    error.getDefaultMessage()
            );
        }

        ex.getBindingResult().getGlobalErrors().forEach(error ->
                errores.putIfAbsent(
                        error.getObjectName(),
                        error.getDefaultMessage()
                )
        );

        Map<String, Object> respuesta = crearRespuesta(
                HttpStatus.BAD_REQUEST,
                "Error de validación",
                "Revisa los datos ingresados"
        );

        respuesta.put("detalles", errores);

        return ResponseEntity.badRequest().body(respuesta);
    }

    // --------------------------------------------------
    // ERRORES DE LAS REGLAS DE NEGOCIO
    // --------------------------------------------------

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException ex
    ) {

        Map<String, Object> respuesta = crearRespuesta(
                HttpStatus.BAD_REQUEST,
                "Solicitud incorrecta",
                ex.getMessage()
        );

        return ResponseEntity.badRequest().body(respuesta);
    }

    // --------------------------------------------------
    // ERRORES DE INTEGRIDAD DE LA BASE DE DATOS
    // --------------------------------------------------

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(
            DataIntegrityViolationException ex
    ) {

        log.warn("Se produjo un conflicto de integridad de datos");

        Map<String, Object> respuesta = crearRespuesta(
                HttpStatus.CONFLICT,
                "Conflicto de datos",
                "Los datos están duplicados o incumplen "
                        + "una restricción de la base de datos"
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(respuesta);
    }

    // --------------------------------------------------
    // JSON INCORRECTO
    // --------------------------------------------------

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {

        Map<String, Object> respuesta = crearRespuesta(
                HttpStatus.BAD_REQUEST,
                "Formato incorrecto",
                "Los datos enviados contienen valores "
                        + "o formatos no válidos"
        );

        return ResponseEntity.badRequest().body(respuesta);
    }

    // --------------------------------------------------
    // PARÁMETROS OBLIGATORIOS FALTANTES
    // --------------------------------------------------

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {

        Map<String, Object> respuesta = crearRespuesta(
                HttpStatus.BAD_REQUEST,
                "Parámetro obligatorio",
                "Falta el parámetro: " + ex.getParameterName()
        );

        return ResponseEntity.badRequest().body(respuesta);
    }

    // --------------------------------------------------
    // PARÁMETROS CON TIPOS INCORRECTOS
    // --------------------------------------------------

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex
    ) {

        Map<String, Object> respuesta = crearRespuesta(
                HttpStatus.BAD_REQUEST,
                "Parámetro incorrecto",
                "El parámetro " + ex.getName()
                        + " contiene un valor no válido"
        );

        return ResponseEntity.badRequest().body(respuesta);
    }

    // --------------------------------------------------
    // ERRORES INESPERADOS DEL SERVIDOR
    // --------------------------------------------------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(
            Exception ex
    ) {

        // El error técnico se registra en el servidor.
        log.error(
                "Error inesperado en ms-clientes: {}",
                ex.getClass().getSimpleName()
        );

        // Android recibe un mensaje seguro.
        Map<String, Object> respuesta = crearRespuesta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor",
                "Ocurrió un error inesperado. "
                        + "Inténtalo nuevamente"
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(respuesta);
    }

    // --------------------------------------------------
    // ESTRUCTURA COMÚN DE LAS RESPUESTAS
    // --------------------------------------------------

    private Map<String, Object> crearRespuesta(
            HttpStatus estado,
            String error,
            String mensaje
    ) {

        Map<String, Object> respuesta = new LinkedHashMap<>();

        respuesta.put("timestamp", LocalDateTime.now());
        respuesta.put("status", estado.value());
        respuesta.put("error", error);
        respuesta.put("mensaje", mensaje);

        return respuesta;
    }
}