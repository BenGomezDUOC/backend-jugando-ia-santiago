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

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final PuntoInteresRepository puntoInteresRepository;
    private final CapsulaRepository capsulaRepository;
    private final ContenidoEducativoRepository contenidoRepository;
    private final VideoRegistradoRepository videoRepository;

    @Override
    public void run(String... args) {
        seedPuntosYCapsulas();
        seedContenidosYVideos();
    }

    private void seedPuntosYCapsulas() {
        if (puntoInteresRepository.count() == 0) {
            List<PuntoInteres> puntos = List.of(
                    PuntoInteres.builder()
                            .id(1L)
                            .codigo("POI-001")
                            .nombre("Plaza de Armas")
                            .descripcion("Corazón cívico e histórico de la capital.")
                            .direccion("Plaza de Armas s/n, Santiago Centro")
                            .latitud(-33.4372)
                            .longitud(-70.6506)
                            .altitud(520.0)
                            .radioToleranciaMetros(50.0)
                            .puntosRecompensa(100)
                            .qrEsperado("QR_PLAZA_ARMAS")
                            .completado(false)
                            .build(),
                    PuntoInteres.builder()
                            .id(2L)
                            .codigo("POI-002")
                            .nombre("Cerro Santa Lucía")
                            .descripcion("Hito fundacional con miradores y jardines históricos.")
                            .direccion("Santa Lucía s/n, Santiago Centro")
                            .latitud(-33.4403)
                            .longitud(-70.6436)
                            .altitud(580.0)
                            .radioToleranciaMetros(60.0)
                            .puntosRecompensa(100)
                            .qrEsperado("QR_SANTA_LUCIA")
                            .completado(false)
                            .build(),
                    PuntoInteres.builder()
                            .id(3L)
                            .codigo("POI-003")
                            .nombre("Cerro San Cristóbal")
                            .descripcion("Gran pulmón verde y mirador panorámico de Santiago.")
                            .direccion("Pío Nono 450, Recoleta")
                            .latitud(-33.4256)
                            .longitud(-70.6347)
                            .altitud(800.0)
                            .radioToleranciaMetros(80.0)
                            .puntosRecompensa(100)
                            .qrEsperado("QR_SAN_CRISTOBAL")
                            .completado(false)
                            .build(),
                    PuntoInteres.builder()
                            .id(4L)
                            .codigo("POI-004")
                            .nombre("Palacio de La Moneda")
                            .descripcion("Sede de gobierno y centro cultural cívico.")
                            .direccion("Moneda s/n, Santiago Centro")
                            .latitud(-33.4429)
                            .longitud(-70.6539)
                            .altitud(521.0)
                            .radioToleranciaMetros(50.0)
                            .puntosRecompensa(100)
                            .qrEsperado("QR_LA_MONEDA")
                            .completado(false)
                            .build(),
                    PuntoInteres.builder()
                            .id(5L)
                            .codigo("POI-005")
                            .nombre("Parque Forestal")
                            .descripcion("Parque lineal tradicional junto al río Mapocho.")
                            .direccion("Ismael Valdés Vergara s/n, Santiago Centro")
                            .latitud(-33.4361)
                            .longitud(-70.6428)
                            .altitud(535.0)
                            .radioToleranciaMetros(70.0)
                            .puntosRecompensa(100)
                            .qrEsperado("QR_PARQUE_FORESTAL")
                            .completado(false)
                            .build(),
                    PuntoInteres.builder()
                            .id(6L)
                            .codigo("POI-006")
                            .nombre("Barrio Bellavista")
                            .descripcion("Polo cultural, artístico y gastronómico de Santiago.")
                            .direccion("Constitución con Dardignac, Providencia/Recoleta")
                            .latitud(-33.4335)
                            .longitud(-70.6358)
                            .altitud(540.0)
                            .radioToleranciaMetros(60.0)
                            .puntosRecompensa(100)
                            .qrEsperado("QR_BELLAVISTA")
                            .completado(false)
                            .build(),
                    PuntoInteres.builder()
                            .id(7L)
                            .codigo("POI-007")
                            .nombre("Museo Nacional de Bellas Artes")
                            .descripcion("Centro neurálgico de las artes visuales chilenas e internacionales.")
                            .direccion("José Miguel de la Barra 650, Santiago Centro")
                            .latitud(-33.4357)
                            .longitud(-70.6436)
                            .altitud(535.0)
                            .radioToleranciaMetros(50.0)
                            .puntosRecompensa(100)
                            .qrEsperado("QR_BELLAS_ARTES")
                            .completado(false)
                            .build(),
                    PuntoInteres.builder()
                            .id(8L)
                            .codigo("POI-008")
                            .nombre("Museo de la Memoria y los Derechos Humanos")
                            .descripcion("Espacio destinado a la reflexión, educación y conmemoración histórica.")
                            .direccion("Matucana 501, Quinta Normal, Santiago")
                            .latitud(-33.4398)
                            .longitud(-70.6811)
                            .altitud(520.0)
                            .radioToleranciaMetros(50.0)
                            .puntosRecompensa(100)
                            .qrEsperado("QR_MUSEO_MEMORIA")
                            .completado(false)
                            .build()
            );
            puntoInteresRepository.saveAll(puntos);
            log.info("Los 8 puntos turísticos de Santiago precargados exitosamente en ms-localizaciones.");
        }

        if (capsulaRepository.count() == 0) {
            List<Capsula> capsulas = List.of(
                    Capsula.builder()
                            .id(1L)
                            .puntoId(1L)
                            .codigoQr("QR_PLAZA_ARMAS")
                            .titulo("¿Qué es la Inteligencia Artificial?")
                            .descripcion("Autor: Magdiel Reynoso. Aprende qué es la inteligencia artificial, cómo reconoce imágenes, comprende preguntas y ayuda a estudiar la historia de Santiago.")
                            .urlVideo("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
                            .categoria("Fundamentos IA")
                            .estado("PUBLICADA")
                            .build(),
                    Capsula.builder()
                            .id(2L)
                            .puntoId(2L)
                            .codigoQr("QR_SANTA_LUCIA")
                            .titulo("Reconocimiento de imágenes")
                            .descripcion("Autor: Fernando Zapata. Descubre cómo la visión artificial reconoce objetos, plantas y monumentos mediante patrones aprendidos de imágenes.")
                            .urlVideo("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4")
                            .categoria("Visión Artificial")
                            .estado("PUBLICADA")
                            .build(),
                    Capsula.builder()
                            .id(3L)
                            .puntoId(3L)
                            .codigoQr("QR_SAN_CRISTOBAL")
                            .titulo("IA y medioambiente")
                            .descripcion("Autor: Juan Pablo Taulis. Conoce cómo la inteligencia artificial analiza datos ambientales, estudia la calidad del aire y ayuda a proteger la naturaleza.")
                            .urlVideo("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4")
                            .categoria("Medioambiente e IA")
                            .estado("PUBLICADA")
                            .build(),
                    Capsula.builder()
                            .id(4L)
                            .puntoId(4L)
                            .codigoQr("QR_LA_MONEDA")
                            .titulo("Aprendizaje automático")
                            .descripcion("Autor: Benjamín Gómez. Aprende qué es el machine learning y cómo los modelos utilizan datos para identificar patrones y realizar predicciones.")
                            .urlVideo("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4")
                            .categoria("Machine Learning")
                            .estado("PUBLICADA")
                            .build(),
                    Capsula.builder()
                            .id(5L)
                            .puntoId(5L)
                            .codigoQr("QR_PARQUE_FORESTAL")
                            .titulo("Redes neuronales")
                            .descripcion("Autor: Juan Pablo Taulis. Descubre cómo funcionan las redes neuronales artificiales y cómo pueden reconocer plantas, voces y otros patrones.")
                            .urlVideo("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
                            .categoria("Redes Neuronales")
                            .estado("PUBLICADA")
                            .build(),
                    Capsula.builder()
                            .id(6L)
                            .puntoId(6L)
                            .codigoQr("QR_BELLAVISTA")
                            .titulo("Inteligencia Artificial generativa")
                            .descripcion("Autor: Fernando Zapata. Aprende cómo la IA generativa puede crear textos, imágenes y sonidos, y por qué debemos utilizarla responsablemente.")
                            .urlVideo("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4")
                            .categoria("IA Generativa")
                            .estado("PUBLICADA")
                            .build(),
                    Capsula.builder()
                            .id(7L)
                            .puntoId(7L)
                            .codigoQr("QR_BELLAS_ARTES")
                            .titulo("Visión Artificial en el Arte y la Cultura")
                            .descripcion("Aprende cómo los algoritmos de visión identifican estilos artísticos, paletas cromáticas y restauración digital de obras patrimoniales.")
                            .urlVideo("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4")
                            .categoria("Visión y Arte")
                            .estado("PUBLICADA")
                            .build(),
                    Capsula.builder()
                            .id(8L)
                            .puntoId(8L)
                            .codigoQr("QR_MUSEO_MEMORIA")
                            .titulo("Preservación Histórica y Archivos con IA")
                            .descripcion("Conoce el rol del procesamiento de lenguaje natural y digitalización inteligente en la catalogación y preservación de documentos históricos.")
                            .urlVideo("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4")
                            .categoria("Historia y NLP")
                            .estado("PUBLICADA")
                            .build()
            );
            capsulaRepository.saveAll(capsulas);
            log.info("Cápsulas educativas de IA precargadas exitosamente en ms-localizaciones.");
        }
    }

    private void seedContenidosYVideos() {
        if (contenidoRepository.count() == 0) {
            ContenidoEducativo contenido1 = ContenidoEducativo.builder()
                    .titulo("Módulo 1: Fundamentos de IA en Santiago")
                    .descripcion("Contenido educativo interactivo para estudiantes y turistas.")
                    .categoria("Fundamentos")
                    .estado("PUBLICADA")
                    .puntoId(1L)
                    .codigoQr("CONT_MODULO_1")
                    .build();
            contenidoRepository.save(contenido1);

            VideoRegistrado video1 = VideoRegistrado.builder()
                    .titulo("Introducción a la IA Urbana")
                    .descripcion("Video explicativo del impacto de la IA en la ciudad.")
                    .urlVideo("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
                    .contenidoId(contenido1.getId())
                    .autor("Equipo Triskeledu")
                    .duracion(180L)
                    .build();
            videoRepository.save(video1);
            log.info("Contenidos educativos y videos de prueba precargados en ms-localizaciones.");
        }
    }
}
