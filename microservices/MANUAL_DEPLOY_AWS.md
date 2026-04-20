# Manual AWS Deployment Guide (No YAML)

This guide uses the class-friendly approach:

1. Build each microservice jar.
2. Create/update each AWS Lambda function manually.
3. Connect them with API Gateway routes.
4. Point frontend to API Gateway URL.

## 1) Build jars

From each service folder:

```powershell
cd user-service
..\..\monolith\mvnw.cmd -DskipTests package
```

```powershell
cd posts-service
..\..\monolith\mvnw.cmd -DskipTests package
```

```powershell
cd stream-service
..\..\monolith\mvnw.cmd -DskipTests package
```

Artifacts:

- `user-service/target/user-service-lambda.jar`
- `posts-service/target/posts-service-lambda.jar`
- `stream-service/target/stream-service-lambda.jar`

## 2) Create 3 Lambda functions

In AWS Console, create three functions:

- `arep-user-service`
- `arep-posts-service`
- `arep-stream-service`

Recommended settings:

- Runtime: Java 17
- Architecture: x86_64 (or arm64 if all deps are tested there)
- Timeout: 20s
- Memory: 512 MB

Set handlers:

- User: `edu.escuelaing.arep.userservice.lambda.UserLambdaHandler::handleRequest`
- Posts: `edu.escuelaing.arep.postsservice.lambda.PostsLambdaHandler::handleRequest`
- Stream: `edu.escuelaing.arep.streamservice.lambda.StreamLambdaHandler::handleRequest`

Upload code:

- Upload corresponding jar to each function.

## 3) Configure environment variables (all Lambdas)

Required:

- `AUTH0_DOMAIN`
- `AUTH0_AUDIENCE`
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `CORS_ALLOWED_ORIGIN`

Optional:

- `STREAM_DEFAULT_LIMIT` (default value used by code is `100`)

## 4) Configure API Gateway routes

Create one API and integrate these routes:

- `POST /api/users` -> `arep-user-service`
- `PUT /api/users/username` -> `arep-user-service`
- `GET /api/me` -> `arep-user-service`
- `POST /api/posts` -> `arep-posts-service`
- `GET /api/feed/public` -> `arep-stream-service`

Enable CORS:

- Allow origin: your frontend URL (S3/CloudFront)
- Allow headers: `Authorization,Content-Type`
- Allow methods: `GET,POST,PUT,OPTIONS`

## 5) Frontend integration

Set frontend env var:

- `VITE_API_URL=<api-gateway-invoke-url>`

Keep Auth0 frontend values aligned with backend:

- `VITE_AUTH0_DOMAIN`
- `VITE_AUTH0_CLIENT_ID`
- `VITE_AUTH0_AUDIENCE`

## 6) Auth0 dashboard checks

In your SPA app settings, add:

- Allowed Callback URLs: frontend URL
- Allowed Logout URLs: frontend URL
- Allowed Web Origins: frontend URL

Your API identifier in Auth0 must match:

- Backend `AUTH0_AUDIENCE`
- Frontend `VITE_AUTH0_AUDIENCE`

## 7) Quick validation

- Login in frontend with Auth0.
- Call `POST /api/users` (should create or return user).
- Call `GET /api/me` with scope `read:profile`.
- Call `POST /api/posts` with scope `write:posts`.
- Call `GET /api/feed/public` (public endpoint).
