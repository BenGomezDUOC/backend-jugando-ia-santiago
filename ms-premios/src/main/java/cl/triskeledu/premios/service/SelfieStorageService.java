
package cl.triskeledu.premios.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import java.util.Iterator;
import java.util.UUID;

@Service
public class SelfieStorageService {

    private static final long MAX_BYTES = 10L * 1024 * 1024;
    private static final int MAX_DIMENSION = 4096;

    private final Path directorio;

    public SelfieStorageService(
            @Value("${app.selfies.directory:./almacenamiento/premios/selfies}")
            String ubicacion
    ) {
        this.directorio = Path.of(ubicacion)
                .toAbsolutePath()
                .normalize();
    }

    // Guardar una imagen real y devolver su referencia.
    public String guardar(MultipartFile archivo, Long usuarioId) {

        if (usuarioId == null || usuarioId <= 0) {
            throw new IllegalArgumentException(
                    "El usuario no es válido"
            );
        }

        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException(
                    "Debes adjuntar una fotografía"
            );
        }

        if (archivo.getSize() > MAX_BYTES) {
            throw new IllegalArgumentException(
                    "La fotografía no puede superar los 10 MB"
            );
        }

        BufferedImage imagen = leerImagen(archivo);

        String nombre = usuarioId
                + "-"
                + UUID.randomUUID()
                + ".jpg";

        Path destino = directorio.resolve(nombre).normalize();

        if (!destino.startsWith(directorio)) {
            throw new IllegalArgumentException(
                    "Nombre de archivo no válido"
            );
        }

        try {
            Files.createDirectories(directorio);

            // Convertir a JPG para guardar una imagen normalizada.
            BufferedImage convertida = new BufferedImage(
                    imagen.getWidth(),
                    imagen.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );

            Graphics2D dibujo = convertida.createGraphics();

            try {
                dibujo.setColor(Color.WHITE);
                dibujo.fillRect(
                        0,
                        0,
                        imagen.getWidth(),
                        imagen.getHeight()
                );
                dibujo.drawImage(imagen, 0, 0, null);
            } finally {
                dibujo.dispose();
            }

            try (OutputStream salida = Files.newOutputStream(
                    destino,
                    StandardOpenOption.CREATE_NEW
            )) {

                if (!ImageIO.write(convertida, "jpg", salida)) {
                    throw new IOException(
                            "No se pudo convertir la fotografía"
                    );
                }
            }

        } catch (IOException ex) {

            eliminar(
                    "/api/premios/gamificacion/selfies/" + nombre
            );

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No se pudo almacenar la fotografía"
            );
        }

        return "/api/premios/gamificacion/selfies/" + nombre;
    }

    // Leer y validar JPG o PNG sin confiar en su extensión.
    private BufferedImage leerImagen(MultipartFile archivo) {

        try {
            byte[] bytes = archivo.getBytes();

            boolean jpeg = bytes.length >= 3
                    && (bytes[0] & 0xFF) == 0xFF
                    && (bytes[1] & 0xFF) == 0xD8
                    && (bytes[2] & 0xFF) == 0xFF;

            boolean png = bytes.length >= 8
                    && (bytes[0] & 0xFF) == 0x89
                    && bytes[1] == 0x50
                    && bytes[2] == 0x4E
                    && bytes[3] == 0x47
                    && bytes[4] == 0x0D
                    && bytes[5] == 0x0A
                    && bytes[6] == 0x1A
                    && bytes[7] == 0x0A;

            if (!jpeg && !png) {
                throw new IllegalArgumentException(
                        "Solo se permiten imágenes JPG o PNG"
                );
            }

            try (ImageInputStream entrada =
                         ImageIO.createImageInputStream(
                                 new ByteArrayInputStream(bytes)
                         )) {

                if (entrada == null) {
                    throw new IllegalArgumentException(
                            "La fotografía no es válida"
                    );
                }

                Iterator<ImageReader> lectores =
                        ImageIO.getImageReaders(entrada);

                if (!lectores.hasNext()) {
                    throw new IllegalArgumentException(
                            "La fotografía no se puede leer"
                    );
                }

                ImageReader lector = lectores.next();

                try {
                    lector.setInput(entrada, true, true);

                    int ancho = lector.getWidth(0);
                    int alto = lector.getHeight(0);

                    if (ancho < 200
                            || alto < 200
                            || ancho > MAX_DIMENSION
                            || alto > MAX_DIMENSION) {

                        throw new IllegalArgumentException(
                                "Las dimensiones de la fotografía no son válidas"
                        );
                    }

                    BufferedImage imagen = lector.read(0);

                    if (imagen == null) {
                        throw new IllegalArgumentException(
                                "No se pudo leer la fotografía"
                        );
                    }

                    return imagen;

                } finally {
                    lector.dispose();
                }
            }

        } catch (IOException ex) {

            throw new IllegalArgumentException(
                    "El archivo no es una fotografía válida",
                    ex
            );
        }
    }

    // Recuperar una fotografía almacenada.
    public Resource obtener(String nombre) {

        if (nombre == null
                || !nombre.matches(
                "[1-9][0-9]*-[a-f0-9-]{36}\\.jpg"
        )) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Fotografía no encontrada"
            );
        }

        Path ruta = directorio.resolve(nombre).normalize();

        if (!ruta.startsWith(directorio)
                || !Files.isRegularFile(ruta)
                || Files.isSymbolicLink(ruta)) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Fotografía no encontrada"
            );
        }

        try {
            Resource recurso = new UrlResource(ruta.toUri());

            if (!recurso.isReadable()) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Fotografía no disponible"
                );
            }

            return recurso;

        } catch (IOException ex) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Fotografía no encontrada"
            );
        }
    }

    // Eliminar una fotografía si falla el registro de la visita.
    public void eliminar(String referencia) {

        if (referencia == null
                || !referencia.startsWith(
                "/api/premios/gamificacion/selfies/"
        )) {
            return;
        }

        String nombre = referencia.substring(
                "/api/premios/gamificacion/selfies/".length()
        );

        if (!nombre.matches(
                "[1-9][0-9]*-[a-f0-9-]{36}\\.jpg"
        )) {
            return;
        }

        try {
            Path ruta = directorio.resolve(nombre).normalize();

            if (ruta.startsWith(directorio)) {
                Files.deleteIfExists(ruta);
            }

        } catch (IOException ex) {
            // No ocultar el error original de la operación.
        }
    }
}

