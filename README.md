# AREP-Microservices

Aplicacion full stack para un flujo tipo Twitter con autenticacion segura en Auth0.

## Estructura general

Este repositorio contiene tres modulos principales:

- `frontend/`: aplicacion web en React + TypeScript + Vite.
- `monolith/`: API backend en Java + Spring Boot.
- `microservices/`: tres funciones Lambda en AWS (user, posts, stream).

## Que hace el sistema

El sistema permite que usuarios autenticados puedan:

- Iniciar y cerrar sesion.
- Publicar mensajes cortos (hasta 140 caracteres).
- Ver un stream publico de publicaciones.
- Consultar su informacion autenticada en un endpoint protegido como `GET /api/me`.

## Stack principal

- Frontend: React, TypeScript, Vite, TanStack Query, Auth0 React SDK.
- Backend: Java 21, Spring Boot, Spring Security, OAuth2 Resource Server, Flyway, PostgreSQL.

## Autenticacion

La autenticacion se implementa con Auth0:

- El frontend obtiene el token de acceso con `@auth0/auth0-react`.
- El backend valida ese JWT como resource server OAuth2.

Variables comunes:

- Backend: `AUTH0_DOMAIN`, `AUTH0_AUDIENCE`
- Frontend: `VITE_AUTH0_DOMAIN`, `VITE_AUTH0_CLIENT_ID`, `VITE_AUTH0_AUDIENCE`

## Documentacion por modulo

Para informacion detallada de instalacion, ejecucion y pruebas:

- Frontend: [frontend/README.md](frontend/README.md)
- Backend (Monolito): [monolith/README.md](monolith/README.md)
- Microservicios (Lambda + API Gateway): [microservices/README.md](microservices/README.md)
