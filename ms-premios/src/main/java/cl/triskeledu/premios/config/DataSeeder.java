package cl.triskeledu.premios.config;

import cl.triskeledu.premios.entity.Premio;
import cl.triskeledu.premios.repository.PremioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final PremioRepository premioRepository;

    @Override
    public void run(String... args) {
        if (premioRepository.count() == 0) {
            List<Premio> premios = List.of(
                    Premio.builder()
                            .nombre("Sticker Holográfico Triskeledu IA")
                            .descripcion("Sticker coleccionable impermeable conmemorativo del circuito de IA en Santiago.")
                            .puntosRequeridos(50)
                            .stockDisponible(100)
                            .imagenUrl("https://raw.githubusercontent.com/google/material-design-icons/master/png/action/stars/materialicons/48dp/2x/baseline_stars_black_48dp.png")
                            .activo(true)
                            .build(),
                    Premio.builder()
                            .nombre("Libreta de Explorador Urbano")
                            .descripcion("Libreta de apuntes ilustrada con hitos arquitectónicos y conceptos de IA.")
                            .puntosRequeridos(150)
                            .stockDisponible(50)
                            .imagenUrl("https://raw.githubusercontent.com/google/material-design-icons/master/png/action/book/materialicons/48dp/2x/baseline_book_black_48dp.png")
                            .activo(true)
                            .build(),
                    Premio.builder()
                            .nombre("Pase Cultural Museos de Santiago")
                            .descripcion("Entrada liberada para dos personas a exposiciones destacadas de Bellas Artes y GAM.")
                            .puntosRequeridos(300)
                            .stockDisponible(25)
                            .imagenUrl("https://raw.githubusercontent.com/google/material-design-icons/master/png/action/confirmation_number/materialicons/48dp/2x/baseline_confirmation_number_black_48dp.png")
                            .activo(true)
                            .build(),
                    Premio.builder()
                            .nombre("Polera Oficial Aprendo con IA")
                            .descripcion("Polera de algodón orgánico con diseño exclusivo del mapa de Santiago IA.")
                            .puntosRequeridos(500)
                            .stockDisponible(15)
                            .imagenUrl("https://raw.githubusercontent.com/google/material-design-icons/master/png/action/card_giftcard/materialicons/48dp/2x/baseline_card_giftcard_black_48dp.png")
                            .activo(true)
                            .build()
            );
            premioRepository.saveAll(premios);
            log.info("Catálogo de premios iniciales precargado exitosamente en ms-premios.");
        }
    }
}
