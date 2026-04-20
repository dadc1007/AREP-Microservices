# AREP-Microservices

Aplicacion full stack compuesta por dos proyectos en la misma raiz:

- `monolith/`: backend en Java con Spring Boot.
- `frontend/`: frontend en React con TypeScript y Vite.

El sistema usa Auth0 para autenticacion. El frontend inicia sesion con Auth0 y el backend valida los JWT emitidos por Auth0 para proteger los endpoints.

## Contexto del proyecto

Este repositorio responde al ejercicio de construir una aplicacion tipo Twitter con un flujo simple de publicaciones publicas y autenticacion segura con Auth0.

El alcance actual del repositorio cubre la base del sistema:

- Un monolito Spring Boot que expone la API REST.
- Un frontend en React que consume la API y gestiona la sesion con Auth0.
- Seguridad basada en JWT emitidos por Auth0.

## Descripcion general

El proyecto implementa una arquitectura separada por responsabilidades:

- El backend expone una API REST para manejar la logica de negocio, persistencia y seguridad.
- El frontend ofrece la interfaz de usuario para interactuar con la API y con la autenticacion.
- PostgreSQL se usa como base de datos.
- Flyway se encarga de las migraciones de esquema.
- Spring Security y OAuth2 Resource Server protegen el backend.

## Requerimiento funcional principal

La aplicacion esta pensada para permitir que usuarios autenticados:

- Inicien y cierren sesion.
- Publiquen mensajes cortos de hasta 140 caracteres.
- Vean un stream publico con todas las publicaciones.
- Consulten su informacion de usuario autenticado mediante un endpoint protegido como `GET /api/me`.

## Entidades principales

El dominio del sistema gira alrededor de tres conceptos:

- `User`: representa al usuario autenticado y su perfil local.
- `Post`: representa una publicacion corta creada por un usuario.
- `Stream`: representa el feed publico global donde se listan las publicaciones.

## Backend

Ubicacion: `monolith/`

Tecnologias principales:

- Java 21
- Spring Boot 4
- Spring Web
- Spring Data JPA
- Spring Security
- OAuth2 Resource Server
- Flyway
- PostgreSQL
- SpringDoc OpenAPI

Responsabilidades:

- Exponer endpoints REST.
- Persistir informacion en PostgreSQL.
- Validar tokens de Auth0.
- Gestionar usuarios y datos del dominio.
- Documentar la API con Swagger/OpenAPI.

## API esperada

La API del monolito esta pensada para organizarse alrededor de estos tipos de endpoints:

- Publicos: lectura del stream publico, por ejemplo `GET /api/posts` o `GET /api/stream`.
- Protegidos: creacion de posts, por ejemplo `POST /api/posts`.
- Protegidos: informacion del usuario actual, por ejemplo `GET /api/me`.

La documentacion OpenAPI esta disponible en Swagger UI.

## Frontend

Ubicacion: `frontend/`

Tecnologias principales:

- React 19
- TypeScript
- Vite
- React Router
- TanStack Query
- HeroUI
- Auth0 React SDK

Responsabilidades:

- Renderizar la interfaz de usuario.
- Autenticar al usuario con Auth0.
- Consumir la API del backend.
- Manejar vistas como feed, perfil y publicacion de contenido.

## Alcance del frontend

La interfaz esta orientada a cubrir estas acciones del usuario:

- Login y logout.
- Crear nuevas publicaciones.
- Ver el stream publico de posts.
- Consumir el token de acceso de Auth0 para llamadas protegidas.

## Autenticacion con Auth0

Auth0 es el proveedor de identidad del sistema.

- El frontend usa el SDK `@auth0/auth0-react` para iniciar sesion, cerrar sesion y obtener el token de acceso.
- El backend recibe el token y lo valida como resource server OAuth2.
- La configuracion se basa en variables de entorno para el dominio, el audience y el client id.

Variables comunes:

- Backend: `AUTH0_DOMAIN`, `AUTH0_AUDIENCE`
- Frontend: `VITE_AUTH0_DOMAIN`, `VITE_AUTH0_CLIENT_ID`, `VITE_AUTH0_AUDIENCE`

## Requisitos

- Java 21
- Maven
- Node.js 20 o superior
- Una instancia de PostgreSQL accesible
- Credenciales de Auth0 configuradas

## Ejecutar el backend

Desde la carpeta raiz del repositorio:

```bash
cd monolith
./mvnw spring-boot:run
```

En Windows tambien puedes usar:

```powershell
cd monolith
.\mvnw.cmd spring-boot:run
```

Antes de arrancar el backend, configura las variables necesarias en un archivo `.env` o en tu entorno local.

## Ejecutar el frontend

Desde la carpeta raiz del repositorio:

```bash
cd frontend
npm install
npm run dev
```

## Estructura del repositorio

```text
.
├── frontend/   # Aplicacion React + TypeScript + Vite
└── monolith/   # Backend Java + Spring Boot
```
