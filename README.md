# 🚀 Backend REST API & MySQL: Aprendo IA Jugando en Santiago

Servicio backend desarrollado en **Spring Boot 3 (Java 21)** con persistencia en **MySQL** para la plataforma de gamificación turística y educativa de **Triskeledu SpA**.

---

## 🛠️ Tecnologías y Dependencias
* **Java:** OpenJDK 21 LTS
* **Framework:** Spring Boot 3.3.5
* **Persistencia:** Spring Data JPA + Hibernate ORM
* **Base de Datos:** MySQL 8.x
* **Driver:** `com.mysql:mysql-connector-j`
* **Validación:** Jakarta Bean Validation (`spring-boot-starter-validation`)
* **Utilidades:** Project Lombok, Java PBKDF2 Hashing (compatible con la app móvil)

---

## 🗄️ Configuración de Base de Datos MySQL

1. Asegúrate de tener tu servidor MySQL iniciado (por ejemplo, mediante XAMPP, Docker o el servicio local de Windows).
2. Crea la base de datos ejecutando en tu cliente MySQL (HeidiSQL, MySQL Workbench, phpMyAdmin o terminal):

```sql
CREATE DATABASE IF NOT EXISTS aprendo_ia_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. Revisa o ajusta las credenciales en [`src/main/resources/application.properties`](src/main/resources/application.properties):

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/aprendo_ia_db?useSSL=false&serverTimezone=America/Santiago&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=
```

---

## 🚀 Cómo Ejecutar el Proyecto

No es necesario tener Maven instalado globalmente, el proyecto incluye el ejecutable de **Maven Wrapper (`mvnw`)**:

### En Windows (PowerShell / CMD):
```powershell
.\mvnw.cmd spring-boot:run
```

### Compilar y generar el JAR ejecutable:
```powershell
.\mvnw.cmd clean package
```

---

## 📋 Precarga de Datos Automática (Data Seeding)
Al iniciar la aplicación por primera vez, el componente `DataSeeder` inserta automáticamente:
* **7 Puntos de Interés de Santiago** (Plaza de Armas, Cerro Santa Lucía, Cerro San Cristóbal, La Moneda, Mercado Central, Bellavista, GAM).
* **4 Cápsulas Educativas Iniciales** con sus códigos QR asociados (`QRTRISK-001-PLAZA`, etc.).
* **Cuentas Institucionales:**
  * **Administrador:** `admin@triskeledu.cl` | Contraseña: `123`
  * **Generador de Contenido:** `contenido@triskeledu.cl` | Contraseña: `123`

---

## 🌐 Endpoints Principales de la API REST

Base URL: `http://localhost:8080/api`

### 1. Usuarios y Autenticación (`/api/usuarios`)
* `POST /api/usuarios/registro`: Registrar nuevo cliente/jugador.
* `POST /api/usuarios/login`: Iniciar sesión y validar hash de contraseña.
* `GET /api/usuarios`: Listar usuarios (permite filtrar por rol `?rol=CLIENTE`).
* `GET /api/usuarios/{id}`: Obtener detalle del usuario.
* `PUT /api/usuarios/{id}/perfil`: Actualizar datos personales y/o contraseña.

### 2. Puntos Turísticos (`/api/puntos`)
* `GET /api/puntos`: Listado ordenado de puntos turísticos con coordenadas GPS y radio de tolerancia.
* `GET /api/puntos/{id}`: Detalle de un punto turístico.
* `POST /api/puntos`: Crear nuevo punto (Rol Administrador).
* `PUT /api/puntos/{id}`: Modificar punto existente.
* `DELETE /api/puntos/{id}`: Eliminar punto turístico.

### 3. Cápsulas Educativas y QR (`/api/capsulas`)
* `GET /api/capsulas`: Listar todas las cápsulas (permite filtrar por `?puntoId=X`).
* `GET /api/capsulas/validar-qr?codigo=QRTRISK-001-PLAZA`: Buscar y validar contenido de un código QR.
* `POST /api/capsulas`: Crear cápsula educativa.

### 4. Gamificación y Misiones (`/api/gamificacion`)
* `POST /api/gamificacion/escanear-qr`:
  * Body: `{"usuarioId": 1, "codigoQr": "QRTRISK-001-PLAZA"}`
  * Valida existencia, evita duplicados y suma **+10 puntos**.
* `POST /api/gamificacion/visita-selfie`:
  * Body: `{"usuarioId": 1, "puntoId": 1, "rutaFotoSelfie": "content://...", "capsulaId": 1}`
  * Registra la evidencia turística, valida que no se duplique y suma **+100 puntos**.
* `POST /api/gamificacion/video-visto?usuarioId=X&capsulaId=Y`: Registra la reproducción completa de un video.
* `GET /api/gamificacion/descubrimientos/{usuarioId}`: Historial de QRs descubiertos por el usuario.
* `GET /api/gamificacion/visitas/{usuarioId}`: Historial de visitas en terreno completadas.

### 5. Notificaciones (`/api/notificaciones`)
* `GET /api/notificaciones/usuario/{usuarioId}`: Listar notificaciones del usuario ordenadas cronológicamente.
* `GET /api/notificaciones/usuario/{usuarioId}/no-leidas/count`: Conteo de notificaciones pendientes.
* `PUT /api/notificaciones/{id}/leer`: Marcar notificación individual como leída.
* `PUT /api/notificaciones/usuario/{usuarioId}/leer-todas`: Marcar todas como leídas.

---

## 🔒 Control de Versiones Git Independiente

Este proyecto está listo para vincularse a su propio repositorio remoto:
```bash
git init
git add .
git commit -m "feat: inicializar backend Spring Boot con MySQL para Aprendo IA Jugando en Santiago"
git branch -M main
# git remote add origin https://github.com/tu-usuario/backend-aprendo-ia.git
# git push -u origin main
```
