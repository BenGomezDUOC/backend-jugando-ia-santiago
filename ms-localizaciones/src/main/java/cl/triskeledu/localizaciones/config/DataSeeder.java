package cl.triskeledu.localizaciones.config;

import cl.triskeledu.localizaciones.entity.Capsula;
import cl.triskeledu.localizaciones.entity.PuntoInteres;
import cl.triskeledu.localizaciones.repository.CapsulaRepository;
import cl.triskeledu.localizaciones.repository.PuntoInteresRepository;
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

    @Override
    public void run(String... args) {
        seedPuntosYCapsulas();
    }

    private void seedPuntosYCapsulas() {
        if (puntoInteresRepository.count() == 0) {
            List<PuntoInteres> puntos = List.of(
                    PuntoInteres.builder()
                            .id(1L)
                            .nombre("Plaza de Armas")
                            .descripcion("Corazón cívico e histórico de la capital.")
                            .latitud(-33.4372)
                            .longitud(-70.6506)
                            .altitud(520.0)
                            .radioToleranciaMetros(50.0)
                            .completado(false)
                            .build(),
                    PuntoInteres.builder()
                            .id(2L)
                            .nombre("Cerro Santa Lucía")
                            .descripcion("Hito fundacional con miradores y jardines históricos.")
                            .latitud(-33.4403)
                            .longitud(-70.6436)
                            .altitud(580.0)
                            .radioToleranciaMetros(60.0)
                            .completado(false)
                            .build(),
                    PuntoInteres.builder()
                            .id(3L)
                            .nombre("Cerro San Cristóbal")
                            .descripcion("Gran pulmón verde y mirador panorámico de Santiago.")
                            .latitud(-33.4256)
                            .longitud(-70.6335)
                            .altitud(860.0)
                            .radioToleranciaMetros(80.0)
                            .completado(false)
                            .build(),
                    PuntoInteres.builder()
                            .id(4L)
                            .nombre("Palacio de La Moneda")
                            .descripcion("Sede de gobierno y epicentro de la historia republicana.")
                            .latitud(-33.4430)
                            .longitud(-70.6538)
                            .altitud(520.0)
                            .radioToleranciaMetros(50.0)
                            .completado(false)
                            .build(),
                    PuntoInteres.builder()
                            .id(5L)
                            .nombre("Mercado Central")
                            .descripcion("Monumento histórico reconocido por su arquitectura en hierro.")
                            .latitud(-33.4336)
                            .longitud(-70.6513)
                            .altitud(515.0)
                            .radioToleranciaMetros(50.0)
                            .completado(false)
                            .build(),
                    PuntoInteres.builder()
                            .id(6L)
                            .nombre("Barrio Bellavista")
                            .descripcion("Epicentro cultural, bohemio y gastronómico.")
                            .latitud(-33.4344)
                            .longitud(-70.6350)
                            .altitud(530.0)
                            .radioToleranciaMetros(70.0)
                            .completado(false)
                            .build(),
                    PuntoInteres.builder()
                            .id(7L)
                            .nombre("GAM (Centro Cultural Gabriela Mistral)")
                            .descripcion("Punto de encuentro para las artes escénicas y visuales.")
                            .latitud(-33.4398)
                            .longitud(-70.6398)
                            .altitud(525.0)
                            .radioToleranciaMetros(50.0)
                            .completado(false)
                            .build()
            );
            puntoInteresRepository.saveAll(puntos);
            log.info("Puntos turísticos de Santiago precargados exitosamente en ms-localizaciones.");
        }

        if (capsulaRepository.count() == 0) {
            List<Capsula> capsulas = List.of(
                    Capsula.builder()
                            .puntoId(1L)
                            .codigoQr("QRTRISK-001-PLAZA")
                            .titulo("Redes Neuronales en el Corazón Cívico")
                            .descripcion("Aprende los principios de las neuronas artificiales.")
                            .urlVideo("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
                            .categoria("Redes Neuronales")
                            .estado("PUBLICADA")
                            .build(),
                    Capsula.builder()
                            .puntoId(2L)
                            .codigoQr("QRTRISK-002-SLUCIA")
                            .titulo("Procesamiento de Lenguaje Natural en las Alturas")
                            .descripcion("Descubre cómo los modelos LLM comprenden el lenguaje.")
                            .urlVideo("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4")
                            .categoria("NLP")
                            .estado("PUBLICADA")
                            .build(),
                    Capsula.builder()
                            .puntoId(3L)
                            .codigoQr("QRTRISK-003-SCRISTOBAL")
                            .titulo("Visión por Computadora desde la Cima")
                            .descripcion("Fundamentos de detección de objetos e imágenes.")
                            .urlVideo("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4")
                            .categoria("Visión Artificial")
                            .estado("PUBLICADA")
                            .build(),
                    Capsula.builder()
                            .puntoId(4L)
                            .codigoQr("QRTRISK-004-MONEDA")
                            .titulo("Ética y Gobernanza en la Inteligencia Artificial")
                            .descripcion("Transparencia y regulación de algoritmos.")
                            .urlVideo("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4")
                            .categoria("Ética IA")
                            .estado("PUBLICADA")
                            .build()
            );
            capsulaRepository.saveAll(capsulas);
            log.info("Cápsulas educativas iniciales precargadas exitosamente en ms-localizaciones.");
        }
    }
}
