package cl.triskeledu.localizaciones.config;

import cl.triskeledu.localizaciones.entity.Capsula;
import cl.triskeledu.localizaciones.entity.ContenidoEducativo;
import cl.triskeledu.localizaciones.entity.PuntoInteres;
import cl.triskeledu.localizaciones.entity.VideoRegistrado;

import cl.triskeledu.localizaciones.repository.CapsulaRepository;
import cl.triskeledu.localizaciones.repository.ContenidoEducativoRepository;
import cl.triskeledu.localizaciones.repository.PuntoInteresRepository;
import cl.triskeledu.localizaciones.repository.VideoRegistradoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final PuntoInteresRepository puntoInteresRepository;
    private final CapsulaRepository capsulaRepository;
    private final ContenidoEducativoRepository contenidoRepository;
    private final VideoRegistradoRepository videoRepository;

    private static final String RECURSOS_ANDROID =
            "android.resource://cl.triskeledu.aprendogame/";

    @Override
    @Transactional
    public void run(String... args) {

        // Inicializar únicamente una base de datos vacía.
        // Nunca sobrescribir información existente.

        if (puntoInteresRepository.count() > 0
                || contenidoRepository.count() > 0
                || videoRepository.count() > 0
                || capsulaRepository.count() > 0) {

            log.info("Se conservan los datos existentes.");
            return;
        }

        // 1. PLAZA DE ARMAS

        registrarInicial(
                "POI-001",
                "Plaza de Armas",
                "Corazón cívico e histórico de la capital.",
                "Plaza de Armas s/n, Santiago Centro",
                -33.4372, -70.6506, 520.0, 50.0,
                "QR_PLAZA_ARMAS",
                "¿Qué es la Inteligencia Artificial?",
                "Autor: Magdiel Reynoso. Aprende qué es la inteligencia artificial, cómo reconoce imágenes, comprende preguntas y ayuda a estudiar la historia de Santiago.",
                "Fundamentos IA",
                "Magdiel Reynoso",
                "video_plaza_armas",
                "miniatura_plaza_armas"
        );

        // 2. CERRO SANTA LUCÍA

        registrarInicial(
                "POI-002",
                "Cerro Santa Lucía",
                "Hito fundacional con miradores y jardines históricos.",
                "Santa Lucía s/n, Santiago Centro",
                -33.4403, -70.6436, 580.0, 60.0,
                "QR_SANTA_LUCIA",
                "Reconocimiento de imágenes",
                "Autor: Fernando Zapata. Descubre cómo la visión artificial reconoce objetos, plantas y monumentos mediante patrones aprendidos de imágenes.",
                "Visión Artificial",
                "Fernando Zapata",
                "video_cerro_santa_lucia",
                "miniatura_santa_lucia"
        );

        // 3. CERRO SAN CRISTÓBAL

        registrarInicial(
                "POI-003",
                "Cerro San Cristóbal",
                "Gran pulmón verde y mirador panorámico de Santiago.",
                "Pío Nono 450, Recoleta",
                -33.4256, -70.6347, 800.0, 80.0,
                "QR_SAN_CRISTOBAL",
                "IA y medioambiente",
                "Autor: Juan Pablo Taulis. Conoce cómo la inteligencia artificial analiza datos ambientales, estudia la calidad del aire y ayuda a proteger la naturaleza.",
                "Medioambiente e IA",
                "Juan Pablo Taulis",
                "video_cerro_san_cristobal",
                "miniatura_san_cristobal"
        );

        // 4. PALACIO DE LA MONEDA

        registrarInicial(
                "POI-004",
                "Palacio de La Moneda",
                "Sede de gobierno y centro cultural cívico.",
                "Moneda s/n, Santiago Centro",
                -33.4429, -70.6539, 521.0, 50.0,
                "QR_LA_MONEDA",
                "Aprendizaje automático",
                "Autor: Benjamín Gómez. Aprende qué es el machine learning y cómo los modelos utilizan datos para identificar patrones y realizar predicciones.",
                "Machine Learning",
                "Benjamín Gómez",
                "video_palacio_de_la_moneda",
                "miniatura_la_moneda"
        );

        // 5. PARQUE FORESTAL

        registrarInicial(
                "POI-005",
                "Parque Forestal",
                "Parque lineal tradicional junto al río Mapocho.",
                "Ismael Valdés Vergara s/n, Santiago Centro",
                -33.4361, -70.6428, 535.0, 70.0,
                "QR_PARQUE_FORESTAL",
                "Redes neuronales",
                "Autor: Juan Pablo Taulis. Descubre cómo funcionan las redes neuronales artificiales y cómo pueden reconocer plantas, voces y otros patrones.",
                "Redes Neuronales",
                "Juan Pablo Taulis",
                "video_parque_forestal",
                "miniatura_parque_forestal"
        );

        // 6. BARRIO BELLAVISTA

        registrarInicial(
                "POI-006",
                "Barrio Bellavista",
                "Polo cultural, artístico y gastronómico de Santiago.",
                "Constitución con Dardignac, Providencia/Recoleta",
                -33.4335, -70.6358, 540.0, 60.0,
                "QR_BELLAVISTA",
                "Inteligencia Artificial generativa",
                "Autor: Fernando Zapata. Aprende cómo la IA generativa puede crear textos, imágenes y sonidos, y por qué debemos utilizarla responsablemente.",
                "IA Generativa",
                "Fernando Zapata",
                "video_barrio_bellavista",
                "miniatura_bellavista"
        );

        log.info(
                "Los seis puntos turísticos originales, "
                        + "contenidos, videos y cápsulas fueron registrados."
        );
    }

    private void registrarInicial(
            String codigoPunto,
            String nombrePunto,
            String descripcionPunto,
            String direccion,
            double latitud,
            double longitud,
            double altitud,
            double radio,
            String codigoQr,
            String tituloContenido,
            String descripcionContenido,
            String categoria,
            String autor,
            String recursoVideo,
            String recursoMiniatura
    ) {

        // 1. Guardar el punto turístico.

        PuntoInteres punto = PuntoInteres.builder()
                .codigo(codigoPunto)
                .nombre(nombrePunto)
                .descripcion(descripcionPunto)
                .direccion(direccion)
                .latitud(latitud)
                .longitud(longitud)
                .altitud(altitud)
                .radioToleranciaMetros(radio)
                .completado(false)
                .puntosRecompensa(100)
                .qrEsperado("")
                .build();

        punto = puntoInteresRepository.saveAndFlush(punto);

        // 2. Guardar el contenido educativo.

        ContenidoEducativo contenido =
                ContenidoEducativo.builder()
                        .titulo(tituloContenido)
                        .descripcion(descripcionContenido)
                        .categoria(categoria)
                        .estado("PUBLICADA")
                        .puntoId(punto.getId())
                        .codigoQr(null)
                        .generadorId(null)
                        .build();

        contenido = contenidoRepository.saveAndFlush(contenido);

        // 3. Registrar el video original de Android.

        String urlVideo =
                RECURSOS_ANDROID + "raw/" + recursoVideo;

        String urlMiniatura =
                RECURSOS_ANDROID + "drawable/" + recursoMiniatura;

        VideoRegistrado video = VideoRegistrado.builder()
                .titulo(tituloContenido)
                .descripcion(descripcionContenido)
                .contenidoId(contenido.getId())
                .urlVideo(urlVideo)
                .urlMiniatura(urlMiniatura)
                .autor(autor)
                .duracion(0L)
                .build();

        video = videoRepository.saveAndFlush(video);

        // 4. Registrar la cápsula y su código QR original.

        Capsula capsula = Capsula.builder()
                .puntoId(punto.getId())
                .contenidoId(contenido.getId())
                .videoId(video.getId())
                .codigoQr(codigoQr)
                .titulo(tituloContenido)
                .descripcion(descripcionContenido)
                .categoria(categoria)
                .urlVideo(urlVideo)
                .estado("PUBLICADA")
                .build();

        capsulaRepository.saveAndFlush(capsula);
    }
}