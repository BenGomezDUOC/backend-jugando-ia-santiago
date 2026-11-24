package cl.triskeledu.localizaciones.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class MultimediaService {

    private static final String PREFIJO =
            "/api/localizaciones/multimedia/";

    private static final long MAX_IMAGEN =
            10L * 1024 * 1024;

    private static final long MAX_VIDEO =
            200L * 1024 * 1024;

    private final Path directorio;

    public MultimediaService(
            @Value("${app.storage.directory}")
            String ruta
    ) throws IOException {

        this.directorio = Path.of(ruta)
                .toAbsolutePath()
                .normalize();

        Files.createDirectories(directorio);
    }

    // Guardar una imagen o video.
    public String guardar(
            MultipartFile archivo,
            String tipo,
            Long usuarioId
    ) {

        if (archivo == null || archivo.isEmpty()) {

            throw new IllegalArgumentException(
                    "Debes seleccionar un archivo"
            );
        }

        boolean esVideo = "video".equals(tipo);

        if (!esVideo && !"imagen".equals(tipo)) {

            throw new IllegalArgumentException(
                    "Tipo de archivo no permitido"
            );
        }

        long limite = esVideo ? MAX_VIDEO : MAX_IMAGEN;

        if (archivo.getSize() > limite) {

            throw new ResponseStatusException(
                    HttpStatus.PAYLOAD_TOO_LARGE,
                    "El archivo supera el tamaño permitido"
            );
        }

        String extension = detectarExtension(
                archivo, esVideo
        );

        String nombre = usuarioId + "-"
                + UUID.randomUUID()
                + extension;

        Path destino = resolverRuta(nombre);

        try (InputStream entrada =
                     archivo.getInputStream()) {

            Files.copy(entrada, destino);

            return PREFIJO + nombre;

        } catch (IOException ex) {

            try {
                Files.deleteIfExists(destino);
            } catch (IOException ignored) {
                // No exponer rutas internas.
            }

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No fue posible guardar el archivo"
            );
        }
    }

    // Comprobar la firma básica del archivo.
    private String detectarExtension(
            MultipartFile archivo,
            boolean esVideo
    ) {

        byte[] bytes;

        try (InputStream entrada =
                     archivo.getInputStream()) {

            bytes = entrada.readNBytes(12);

        } catch (IOException ex) {

            throw new IllegalArgumentException(
                    "No fue posible leer el archivo"
            );
        }

        if (esVideo
                && bytes.length >= 12
                && bytes[4] == 'f'
                && bytes[5] == 't'
                && bytes[6] == 'y'
                && bytes[7] == 'p') {

            return ".mp4";
        }

        if (!esVideo && bytes.length >= 8) {

            // Firma JPEG.
            if ((bytes[0] & 0xFF) == 0xFF
                    && (bytes[1] & 0xFF) == 0xD8
                    && (bytes[2] & 0xFF) == 0xFF) {

                return ".jpg";
            }

            // Firma PNG.
            if ((bytes[0] & 0xFF) == 0x89
                    && bytes[1] == 'P'
                    && bytes[2] == 'N'
                    && bytes[3] == 'G'
                    && bytes[4] == 13
                    && bytes[5] == 10
                    && bytes[6] == 26
                    && bytes[7] == 10) {

                return ".png";
            }
        }

        throw new IllegalArgumentException(
                esVideo
                        ? "Solo se permiten videos MP4"
                        : "Solo se permiten imágenes JPG o PNG"
        );
    }

    // Recuperar un archivo almacenado.
    public Resource obtener(String nombre) {

        Path ruta = resolverRuta(nombre);

        try {

            if (!Files.isRegularFile(ruta)
                    || Files.isSymbolicLink(ruta)) {

                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Archivo no encontrado"
                );
            }

            Resource recurso =
                    new UrlResource(ruta.toUri());

            if (!recurso.isReadable()) {

                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Archivo no disponible"
                );
            }

            return recurso;

        } catch (IOException ex) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Archivo no disponible"
            );
        }
    }

    // Validar una referencia multimedia del servidor.
    public boolean esReferenciaLocal(String referencia) {

        return referencia != null
                && referencia.startsWith(PREFIJO);
    }

    public void validarReferencia(
            String referencia,
            String tipo,
            Long usuarioId,
            boolean administrador
    ) {

        if (!esReferenciaLocal(referencia)) {

            throw new IllegalArgumentException(
                    "Referencia multimedia no válida"
            );
        }

        String nombre =
                referencia.substring(PREFIJO.length());

        if (!administrador
                && !nombre.startsWith(usuarioId + "-")) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El archivo pertenece a otro usuario"
            );
        }

        boolean extensionValida = "video".equals(tipo)
                ? nombre.endsWith(".mp4")
                : nombre.endsWith(".jpg")
                || nombre.endsWith(".png");

        if (!extensionValida) {

            throw new IllegalArgumentException(
                    "El tipo de archivo no corresponde"
            );
        }

        obtener(nombre);
    }

    // Impedir que se acceda a archivos fuera del directorio.
    private Path resolverRuta(String nombre) {

        if (nombre == null
                || !nombre.matches(
                "[1-9][0-9]*-[a-f0-9-]{36}\\.(mp4|jpg|png)"
        )) {

            throw new IllegalArgumentException(
                    "Nombre de archivo no válido"
            );
        }

        Path ruta = directorio
                .resolve(nombre)
                .normalize();

        if (!ruta.startsWith(directorio)) {

            throw new IllegalArgumentException(
                    "Ruta no permitida"
            );
        }

        return ruta;
    }
}
