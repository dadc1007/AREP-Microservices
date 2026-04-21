# Frontend - AREP-Microservices

Aplicacion web del proyecto, construida con React + TypeScript + Vite.

## Objetivo

Este modulo implementa la interfaz de usuario para:

- Login y logout con Auth0.
- Creacion de nuevas publicaciones.
- Visualizacion del stream publico de posts.
- Consumo de endpoints protegidos usando el access token.

## Tecnologias principales

- React 19
- TypeScript
- Vite
- React Router
- TanStack Query
- HeroUI
- Auth0 React SDK (`@auth0/auth0-react`)
- Vitest

## Variables de entorno

Configura las siguientes variables para el SDK de Auth0 y la API:

- `VITE_AUTH0_DOMAIN`
- `VITE_AUTH0_CLIENT_ID`
- `VITE_AUTH0_AUDIENCE`
- `VITE_API_BASE_URL`

## Requisitos

- Node.js 20 o superior
- npm

## Instalacion y ejecucion

Desde la carpeta raiz del repositorio:

```bash
cd frontend
npm install
npm run dev
```

## Pruebas

Ubicaciones principales:

- `tests/features/...`
- `tests/shared/api/...`

Incluyen validaciones de:

- Componentes del feed/post.
- Servicios por feature (`post`, `stream`, `user`).
- Manejo de errores en capa API compartida.

## Estructura de alto nivel

```text
frontend/
├── src/
│   ├── app/
│   ├── features/
│   ├── shared/
│   └── styles/
├── tests/
└── package.json
```

## Bitacora de despliegue (S3, CloudFront y Vercel)

Durante el despliegue del frontend en AWS, primero hicimos publicacion directa en S3 como sitio estatico.

Ese enfoque no se dejo como solucion final porque la exposicion quedaba por HTTP y, en ese escenario, la autenticacion con Auth0 presentaba problemas de funcionamiento.

Despues intentamos resolver el tema de HTTPS poniendo CloudFront delante de S3, pero no fue posible completarlo por permisos en la cuenta/recursos de AWS.

Como cierre, se desplego el frontend en Vercel, donde el sitio quedo funcionando con HTTPS y compatible con el flujo de autenticacion.

### Evidencia (pantallazos)

Pantallazo 1 - Intento de despliegue en S3 (hosting estatico)

![[Espacio para pantallazo]](assets/S3.png)

Pantallazo 2 - Sitio expuesto por HTTP y error de Auth0

Error exacto en la consola:

```
index-BhDrXzGP.js:10 Uncaught Error:
  auth0-spa-js must run on a secure origin. See https://github.com/auth0/auth0-spa-js/blob/main/FAQ.md#why-do-i-get-auth0-spa-js-must-run-on-a-secure-origin for more information.

  at index-BhDrXzGP.js:10:102925
  at new As (index-BhDrXzGP.js:12:9)
  at index-BhDrXzGP.js:12:24103
  at Jo (index-BhDrXzGP.js:8:52346)
  at Object.useState (index-BhDrXzGP.js:8:60376)
  at e.useState (index-BhDrXzGP.js:1:8757)
  at qs (index-BhDrXzGP.js:12:24081)
  at To (index-BhDrXzGP.js:8:47557)
  at vc (index-BhDrXzGP.js:8:70080)
  at Fc (index-BhDrXzGP.js:8:80359)
```

[Espacio para pantallazo]

Pantallazo 3 - Intento de configuracion de CloudFront (bloqueo por permisos)

![[Espacio para pantallazo]](assets/cloudFront.png)

Pantallazo 4 - Despliegue final en Vercel con HTTPS

![[Espacio para pantallazo]](assets/vercel.png)

## Relacion con el backend

Este modulo consume la API del backend ubicada en dos lugares:

- API REST del monolito: [../monolith/README.md](../monolith/README.md)
- API Gateway con Lambdas: [../microservices/README.md](../microservices/README.md)
