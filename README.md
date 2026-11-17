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
       │ • Gestión de Clientes │◀────│ • Gamificación        │────▶│ • Puntos de Interés   │
       │ • Cuentas y Roles     │REST │ • Escaneo QR (+10 pts)│REST │ • Cápsulas de IA      │
       │ • PBKDF2 Criptografía │     │ • Selfies (+100 pts)  │     │ • Contenido Educativo │
       │ • Puntos Acumulados   │     │ • Notificaciones Push │     │ • Validación de QR    │
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
* **Responsabilidad:** Registro de usuarios/clientes, autenticación segura con algoritmo PBKDF2 compatible con la app Android, perfiles, roles (`ADMINISTRADOR`, `GENERADOR`, `CLIENTE`), y saldo de puntos acumulados.
* **Cuentas Precargadas:**
  * Admin: `admin@triskeledu.cl` / `123`
  * Generador: `contenido@triskeledu.cl` / `123`
  * Cliente Demo: `estudiante@triskeledu.cl` / `123`
* **Endpoints Principales:**
  * `POST /api/clientes/usuarios/registro` : Registrar nueva cuenta
  * `POST /api/clientes/usuarios/login` : Login y verificación de contraseña PBKDF2
  * `GET /api/clientes/usuarios/{id}` : Obtener perfil de usuario
  * `PUT /api/clientes/usuarios/{id}/perfil` : Actualizar perfil o clave
  * `PUT /api/clientes/usuarios/{id}/sumar-puntos?puntos={pts}` : Sumar puntos (llamado internamente por `ms-premios`)

---

### 2. `ms-premios` (Puerto `8082`)
* **Base de Datos:** `bd_triskeledu_premios`
* **Responsabilidad:** Sistema de gamificación, registro de escaneos QR (+10 puntos), validación de evidencias/selfies en terreno (+100 puntos), historial de cápsulas/videos vistos y gestión de notificaciones.
* **Comunicación Inter-servicio:**
  * Consulta a `ms-localizaciones` (`http://localhost:8083`) para validar que el QR pertenezca a una cápsula existente.
  * Llama a `ms-clientes` (`http://localhost:8081`) para sumar los puntos al usuario correspondiente.
* **Endpoints Principales:**
  * `POST /api/premios/gamificacion/escanear-qr` : Escanear código QR (+10 pts)
  * `POST /api/premios/gamificacion/visita-selfie` : Registrar evidencia selfie (+100 pts)
  * `POST /api/premios/gamificacion/video-visto` : Registrar visualización de video educativo
  * `GET /api/premios/gamificacion/descubrimientos/{usuarioId}` : Historial de QR descubiertos
  * `GET /api/premios/gamificacion/visitas/{usuarioId}` : Historial de visitas y fotos
  * `GET /api/premios/notificaciones/usuario/{usuarioId}` : Bandeja de notificaciones
  * `GET /api/premios/notificaciones/usuario/{usuarioId}/no-leidas/count` : Contador de no leídas
  * `PUT /api/premios/notificaciones/{id}/leer` : Marcar notificación como leída

---

### 3. `ms-localizaciones` (Puerto `8083`)
* **Base de Datos:** `bd_triskeledu_localizaciones`
* **Responsabilidad:** Gestión de coordenadas GPS de los 7 puntos turísticos de Santiago (Plaza de Armas, Santa Lucía, San Cristóbal, La Moneda, Mercado Central, Bellavista, GAM), radios de tolerancia geográfica (50m - 80m), catálogo de cápsulas de Inteligencia Artificial y contenidos educativos multimedia.
* **Endpoints Principales:**
  * `GET /api/localizaciones/puntos` : Lista completa de puntos turísticos
  * `GET /api/localizaciones/puntos/{id}` : Detalle de un punto turístico
  * `GET /api/localizaciones/capsulas` : Catálogo de cápsulas educativas
  * `GET /api/localizaciones/capsulas/validar-qr?codigo={codigo}` : Validación de código QR
  * `POST /api/localizaciones/puntos` : Crear nuevo punto (Generador/Admin)
  * `POST /api/localizaciones/capsulas` : Crear nueva cápsula (Generador/Admin)

---

## 🚀 Requisitos y Ejecución

### Prerrequisitos
1. **Java JDK 21 LTS** o superior.
2. **Servidor MySQL** activo en `localhost:3306` (por ejemplo vía XAMPP o servicio local de MySQL).

### Compilar Todo el Proyecto
Desde la raíz del repositorio (`backend-aprendo-ia`):
```bash
.\mvnw.cmd clean compile
```

### Ejecutar los Microservicios

Puedes iniciar cada microservicio en una terminal independiente según lo necesites:

**Terminal 1 - ms-clientes:**
```bash
.\mvnw.cmd spring-boot:run -pl ms-clientes
```

**Terminal 2 - ms-localizaciones:**
```bash
.\mvnw.cmd spring-boot:run -pl ms-localizaciones
```

**Terminal 3 - ms-premios:**
```bash
.\mvnw.cmd spring-boot:run -pl ms-premios
```
