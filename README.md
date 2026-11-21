# Backend Triskeledu - Aprendo con IA en Santiago (Microservicios)

Arquitectura de **3 microservicios independientes** con **Spring Boot 3 (Java 21)** y persistencia de datos relacional en **MySQL (Database-per-Service pattern)** para el ecosistema educativo y gamificado de la aplicación móvil *Aprendo con IA en Santiago*.

---

## 🏛️ Arquitectura de Microservicios

```
                                 ┌──────────────────────────────────────────────┐
                                 │            Aplicación Android                │
                                 │        (Aprendo con IA en Santiago)          │
                                 └───────────────┬──────────────────────────────┘
                                                 │
                   ┌─────────────────────────────┼─────────────────────────────┐
                   │ (HTTP / JSON)               │ (HTTP / JSON)               │ (HTTP / JSON)
                   ▼                             ▼                             ▼
       ┌───────────────────────┐     ┌───────────────────────┐     ┌───────────────────────┐
       │      ms-clientes      │     │      ms-premios       │     │   ms-localizaciones   │
       │     (Puerto 8081)     │     │     (Puerto 8082)     │     │     (Puerto 8083)     │
       │                       │     │                       │     │                       │
       │ • Gestión de Clientes │◀────│ • Gamificación        │────▶│ • 8 Puntos de Interés │
       │ • Cuentas y Roles     │REST │ • Escaneo QR (+10 pts)│REST │ • Cápsulas de IA      │
       │ • PBKDF2 Criptografía │     │ • Selfies (+100 pts)  │     │ • Contenido Educativo │
       │ • Puntos Acumulados   │     │ • Catálogo de Premios │     │ • Videos Registrados  │
       │ • DTOs Flexibles      │     │ • Notificaciones Push │     │ • Validación QR y POI │
       └───────────┬───────────┘     └───────────┬───────────┘     └───────────┬───────────┘
                   │                             │                             │
                   ▼                             ▼                             ▼
       ┌───────────────────────┐     ┌───────────────────────┐     ┌───────────────────────┐
       │      MySQL DB:        │     │      MySQL DB:        │     │      MySQL DB:        │
       │ bd_triskeledu_clientes│     │  bd_triskeledu_premios│     │bd_triskeledu_localiz. │
       └───────────────────────┘     └───────────────────────┘     └───────────────────────┘
```

---

## 📦 Detalle de los Microservicios

### 1. `ms-clientes` (Puerto `8081`)
* **Base de Datos:** `bd_triskeledu_clientes`
* **Compatibilidad de DTOs:** Admite indistintamente la nomenclatura estándar y la nomenclatura de Android (`nombre`/`nombreCompleto`, `correo`/`correoElectronico`, `avatar`/`avatarSeleccionado`, `institucion`, `rol` `CLIENTE`/`ESTUDIANTE`/`ADMINISTRADOR`/`GENERADOR`).
* **Criptografía:** Algoritmo PBKDF2 compatible con Android.
* **Cuentas Precargadas:**
  * Admin: `admin@triskeledu.cl` / `123`
  * Generador: `contenido@triskeledu.cl` / `123`
  * Estudiante/Cliente Demo: `estudiante@triskeledu.cl` / `123`
* **Endpoints:**
  * `POST /api/clientes/usuarios/registro` : Registro de usuario
  * `POST /api/clientes/usuarios/login` : Login seguro con PBKDF2
  * `GET /api/clientes/usuarios/{id}` : Perfil de usuario con `nivelActual` calculado
  * `PUT /api/clientes/usuarios/{id}/perfil` : Actualización de perfil
  * `PUT /api/clientes/usuarios/{id}/sumar-puntos?puntos={pts}` : Suma o deducción de puntos

---

### 2. `ms-premios` (Puerto `8082`)
* **Base de Datos:** `bd_triskeledu_premios`
* **Reglas de Negocio Implementadas:**
  * **QR Nuevo (+10 pts):** Valida que el código QR pertenezca al punto turístico seleccionado (`puntoIdSeleccionado`) y que la cápsula esté en estado `PUBLICADA`. Si ya fue descubierto, no suma puntos repetidos (+0 pts).
  * **Selfie en Terreno (+100 pts):** Valida que la fotografía de evidencia sea obligatoria. Se bonifica una vez por cápsula/QR específico (`(usuarioId, capsulaId)`), permitiendo completar diferentes cápsulas del mismo punto turístico.
  * **Catálogo de Premios:** Endpoint `GET /api/premios` compatible con `PremiosApiService` de Retrofit (`PremioDto`: `id`, `nombre`, `descripcion`, `puntosRequeridos`, `stockDisponible`, `imagenUrl`). Permite canje con deducción de saldo y notificación.
* **Endpoints:**
  * `GET /api/premios` : Catálogo de premios físicos disponibles para canje
  * `GET /api/premios/{id}` : Detalle de un premio
  * `POST /api/premios/canjear?usuarioId={id}&premioId={id}` : Canjear premio
  * `POST /api/premios/gamificacion/escanear-qr` : Escanear QR (+10 pts)
  * `POST /api/premios/gamificacion/visita-selfie` : Registrar selfie (+100 pts)
  * `POST /api/premios/gamificacion/video-visto` : Registrar visualización de video
  * `GET /api/premios/gamificacion/descubrimientos/{usuarioId}` : Historial de QR descubiertos
  * `GET /api/premios/gamificacion/visitas/{usuarioId}` : Historial de visitas y selfies
  * `GET /api/premios/notificaciones/usuario/{usuarioId}` : Notificaciones del jugador
  * `PUT /api/premios/notificaciones/{id}/leer` : Marcar notificación leída

---

### 3. `ms-localizaciones` (Puerto `8083`)
* **Base de Datos:** `bd_triskeledu_localizaciones`
* **Puntos Turísticos (8 POIs Oficiales sincronizados con Android):**
  1. Plaza de Armas (`QR_PLAZA_ARMAS`)
  2. Cerro Santa Lucía (`QR_SANTA_LUCIA`)
  3. Cerro San Cristóbal (`QR_SAN_CRISTOBAL`)
  4. Palacio de La Moneda (`QR_LA_MONEDA`)
  5. Parque Forestal (`QR_PARQUE_FORESTAL`)
  6. Barrio Bellavista (`QR_BELLAVISTA`)
  7. Museo Nacional de Bellas Artes (`QR_BELLAS_ARTES`)
  8. Museo de la Memoria y los Derechos Humanos (`QR_MUSEO_MEMORIA`)
* **Atributos de POI:** Incluye `direccion`, `codigo`, `qrEsperado`, `puntosRecompensa`, `radioToleranciaMetros`, `altitud`, `latitud`, `longitud`.
* **Cápsulas Educativas:** Las 6 cápsulas oficiales de IA creadas por el equipo (IA, Visión Artificial, Medioambiente, Machine Learning, Redes Neuronales, IA Generativa) más cápsulas para Bellas Artes y Museo de la Memoria.
* **Gestión de Contenido:** Soporta CRUD completo de `ContenidoEducativo` y `VideoRegistrado` para el Administrador y Generador de Contenido.
* **Endpoints:**
  * `GET /api/localizaciones/puntos-interes` (y alias `/api/localizaciones/puntos`)
  * `GET /api/localizaciones/puntos-interes/{id}`
  * `POST /api/localizaciones/puntos-interes`
  * `PUT /api/localizaciones/puntos-interes/{id}`
  * `DELETE /api/localizaciones/puntos-interes/{id}`
  * `GET /api/localizaciones/capsulas`
  * `GET /api/localizaciones/capsulas/validar-qr?codigo={codigo}`
  * `POST /api/localizaciones/capsulas`
  * `GET /api/localizaciones/contenidos` (CRUD contenidos)
  * `GET /api/localizaciones/videos` (CRUD videos)

---

## 🚀 Requisitos y Ejecución

### Prerrequisitos
1. **Java JDK 21 LTS**.
2. **Servidor MySQL** activo en `localhost:3306`.

### Compilar Todo el Proyecto
```powershell
.\mvnw.cmd clean compile
```

### Ejecutar los Microservicios
```powershell
# Terminal 1:
.\mvnw.cmd spring-boot:run -pl ms-clientes

# Terminal 2:
.\mvnw.cmd spring-boot:run -pl ms-localizaciones

# Terminal 3:
.\mvnw.cmd spring-boot:run -pl ms-premios
```
