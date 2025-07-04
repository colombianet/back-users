# 🏠 InmoHouse Backend

Aplicación backend para la gestión inmobiliaria desarrollada como parte de la prueba técnica. Implementa autenticación con JWT, control de acceso por roles (`ADMIN`, `AGENTE`, `CLIENTE`) y funcionalidades CRUD sobre propiedades y usuarios usando Spring Boot y MySQL.

## 🚀 Tecnologías

- Java 17
- Spring Boot 3
- Spring Security 6
- JWT (JSON Web Token)
- MySQL
- Maven

## 🔐 Autenticación y autorización

- Endpoint de autenticación: `/api/auth/login`
- Devuelve un JWT firmado con clave secreta
- El token se debe incluir en cada petición protegida:
Authorization: Bearer <token>
- Acceso controlado por rol (`ROLE_ADMIN`, `ROLE_AGENTE`, `ROLE_CLIENTE`)

## 🛠️ Seguridad con Spring Security

Se implementa `JwtAuthFilter` que:

- Intercepta las peticiones HTTP
- Extrae el token JWT del encabezado
- Valida el token y recupera el usuario desde la base de datos
- Convierte roles en authorities (`ROLE_ADMIN`, etc.)
- Establece el contexto de autenticación

Los endpoints están protegidos con `@PreAuthorize("hasRole(...)")`.

## 🔐 Endpoints protegidos

| Endpoint                           | Método | Rol requerido  | Descripción                                   |
|------------------------------------|--------|----------------|-----------------------------------------------|
| `/api/auth/login`                  | POST   | Público        | Inicia sesión y devuelve token JWT            |
| `/api/users`                       | GET    | ADMIN          | Lista todos los usuarios                      |
| `/api/users/{id}`                  | GET    | ADMIN          | Muestra un usuario por ID                     |
| `/api/users`                       | POST   | ADMIN          | Crea un nuevo usuario                         |
| `/api/users/{id}`                  | PUT    | ADMIN          | Actualiza usuario existente                   |
| `/api/users/{id}`                  | DELETE | ADMIN          | Elimina usuario                               |
| `/api/propiedades`                 | GET    | Todos          | Lista todas las propiedades                   |
| `/api/propiedades`                 | POST   | ADMIN / AGENTE | Crea propiedad (se asocia si el agente está autenticado) |
| `/api/agente/mis-propiedades`      | GET    | AGENTE         | Lista propiedades del agente autenticado      |
| `/api/agente/crear-propiedad`      | POST   | AGENTE         | Crea nueva propiedad para el agente autenticado |
| `/api/cliente/disponibles`         | GET    | CLIENTE        | Lista propiedades disponibles (`estado = DISPONIBLE`) |

## 🧪 Ejemplo de uso con Postman

1. Realiza login:

 POST http://localhost:8080/api/auth/login

 Body:
 {
   "email": "oscar@mail.com",
   "password": "12345"
 }

2. Copia el JWT del response

3. En las peticiones siguientes, agrega:
Authorization: Bearer <el-token>

## Configuración
1. Duplica el archivo de configuración de ejemplo:
cp src/main/resources/application-example.properties src/main/resources/application.properties
2. Configura tus propiedades en application.properties:
jwt.secret=tu-clave-secreta
jwt.expiration=86400000
spring.datasource.url=jdbc:mysql://localhost:3306/inmohouse
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña

## Cómo funciona la seguridad
- El filtro JwtAuthFilter valida el JWT en cada request

- Si es válido, carga el usuario y sus roles desde la base de datos

- Se configuran las authorities en el contexto de Spring

- Los métodos protegidos usan @PreAuthorize para validar acceso por rol

- Si el rol no es suficiente, se devuelve 403 Forbidden

Estructura del proyecto

controllers: separados por tipo de usuario (User, Agente, Cliente)

entities: modelos JPA (User, Propiedad) con relaciones

repositories: acceso a datos con Spring Data

services: lógica de negocio para propiedades y usuarios

security: configuración JWT y filtros personalizados

## ✅ Buenas prácticas aplicadas
Contraseñas encriptadas con BCrypt

Tokens con expiración limitada y clave secreta externa

Uso de application-example.properties como referencia segura

Seguridad por rol en cada controlador

Organización del backend basada en principios de arquitectura limpia

## 👨‍💻 Autor
Oscar Pérez.
