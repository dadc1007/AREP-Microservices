# Lambda Microservices

This folder contains exactly 3 Java microservices implemented as AWS Lambda functions:

- `user-service` (`UserLambdaHandler`)
- `posts-service` (`PostsLambdaHandler`)
- `stream-service` (`StreamLambdaHandler`)

This project can be deployed in two ways:

- Manual way (recommended for class flow): create each Lambda in AWS and upload jar.
- SAM way (optional): use `template.yaml` to automate infra.

## Endpoints

- `POST /api/users`
- `PUT /api/users/username`
- `GET /api/me`
- `POST /api/posts`
- `GET /api/feed/public`

## Build per service

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

Generated jars:

- `user-service/target/user-service-lambda.jar`
- `posts-service/target/posts-service-lambda.jar`
- `stream-service/target/stream-service-lambda.jar`

## Manual deploy to AWS Lambda (no YAML required)

Create one Lambda function per service in AWS Console:

- Runtime: Java 17
- Upload type: .zip or .jar
- Handler (User): `edu.escuelaing.arep.userservice.lambda.UserLambdaHandler::handleRequest`
- Handler (Posts): `edu.escuelaing.arep.postsservice.lambda.PostsLambdaHandler::handleRequest`
- Handler (Stream): `edu.escuelaing.arep.streamservice.lambda.StreamLambdaHandler::handleRequest`

Environment variables for each function:

- `AUTH0_DOMAIN`
- `AUTH0_AUDIENCE`
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `CORS_ALLOWED_ORIGIN`
- `STREAM_DEFAULT_LIMIT` (optional)

Then create API Gateway (HTTP API or REST API) and map routes:

- `POST /api/users` -> User Lambda
- `PUT /api/users/username` -> User Lambda
- `GET /api/me` -> User Lambda
- `POST /api/posts` -> Posts Lambda
- `GET /api/feed/public` -> Stream Lambda

Important:

- Enable CORS in API Gateway for your frontend origin.
- Configure Lambda invoke permissions from API Gateway (if AWS does not auto-create them).
- Use the API Gateway invoke URL as frontend `VITE_API_URL`.

## Optional: build and deploy with SAM

If you prefer infrastructure-as-code, this repo also includes `template.yaml`:

```powershell
cd ..\microservices
sam build
sam deploy --guided
```

## Environment variables

Required:

- `AUTH0_DOMAIN`
- `AUTH0_AUDIENCE`
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `CORS_ALLOWED_ORIGIN`

Optional:

- `STREAM_DEFAULT_LIMIT` (default: `100`)
