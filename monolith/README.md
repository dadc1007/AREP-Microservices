# Monolith - AREP-Microservices

Backend del proyecto implementado con Java y Spring Boot.

## Objetivo

Este modulo expone la API REST del sistema para:

- Gestion de publicaciones.
- Consulta del stream publico.
- Exposicion de informacion del usuario autenticado.
- Validacion de JWT emitidos por Auth0.

## Tecnologias principales

- Java 21
- Spring Boot 4
- Spring Web
- Spring Data JPA
- Spring Security
- OAuth2 Resource Server
- Flyway
- PostgreSQL
- SpringDoc OpenAPI

## Dominio funcional

Entidades principales:

- `User`: usuario autenticado y perfil local.
- `Post`: publicacion corta creada por un usuario.
- `Stream`: feed publico global de publicaciones.

## Endpoints esperados

La API esta organizada en:

- Publicos: lectura de stream y posts, por ejemplo `GET /api/posts` o `GET /api/stream`.
- Protegidos: creacion de posts, por ejemplo `POST /api/posts`.
- Protegidos: informacion del usuario actual, por ejemplo `GET /api/me`.

La documentacion OpenAPI se expone por Swagger UI.

## Variables de entorno

Configura estas variables para autenticacion y base de datos:

- `AUTH0_DOMAIN`
- `AUTH0_AUDIENCE`
- Variables de conexion a PostgreSQL segun `application*.properties`

## Requisitos

- Java 21
- Maven
- PostgreSQL disponible

## Ejecucion

Desde la carpeta raiz del repositorio:

```bash
cd monolith
./mvnw spring-boot:run
```

En Windows:

```powershell
cd monolith
.\mvnw.cmd spring-boot:run
```

## Pruebas

Ubicacion principal de tests:

- `src/test/java/...`

Incluyen pruebas de:

- Controladores (`PostController`, `FeedController`, `UserController`, `MeController`).
- Servicios (`PostServiceImpl`, `FeedServiceImpl`, `UserServiceImpl`).
- Configuracion, mapeadores, DTOs, entidades y manejo de excepciones.

Ejecutar pruebas:

```bash
cd monolith
./mvnw test
```

En Windows:

```powershell
cd monolith
.\mvnw.cmd test
```
