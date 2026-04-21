# Deployment Values Checklist

Fill in your actual values here before starting deployment. Copy-paste directly from here into AWS Console.

## Neon Database

```
Pooled Host: ep-lingering-sunset-amx9eghn-pooler.c-5.us-east-1.aws.neon.tech
Database: neondb
User: neondb_owner
Password: _____________________________ (your reset password)
```

## AWS Lambda Environment Variables (same for all 3 Lambda functions)

```
DB_URL=jdbc:postgresql://ep-lingering-sunset-amx9eghn-pooler.c-5.us-east-1.aws.neon.tech/neondb?sslmode=require

DB_USERNAME=neondb_owner

DB_PASSWORD=_____________________________ (your reset password)

AUTH0_DOMAIN=_____________________________ (e.g., company.auth0.com)

AUTH0_AUDIENCE=_____________________________ (e.g., https://arep-api)

CORS_ALLOWED_ORIGIN=_____________________________ (e.g., https://my-app.s3.amazonaws.com)
```

## Auth0

```
Tenant: _____________________________
Domain: _____________________________ (same as AUTH0_DOMAIN)
Client ID: _____________________________ (for frontend)
API Identifier: _____________________________ (same as AUTH0_AUDIENCE)
```

## Frontend (after deployment)

After API Gateway is created and deployed, you'll get an Invoke URL. Update frontend/.env:

```
VITE_API_URL=https://_____________________________.execute-api.us-east-1.amazonaws.com/prod
VITE_AUTH0_DOMAIN=_____________________________
VITE_AUTH0_CLIENT_ID=_____________________________
VITE_AUTH0_AUDIENCE=_____________________________
```

## AWS Lambda Function Names (exact names to use)

```
arep-user-service
arep-posts-service
arep-stream-service
```

## AWS Lambda Handler Class Names (exact, copy-paste from here)

```
User Service:   edu.escuelaing.arep.userservice.lambda.UserLambdaHandler::handleRequest
Posts Service:  edu.escuelaing.arep.postsservice.lambda.PostsLambdaHandler::handleRequest
Stream Service: edu.escuelaing.arep.streamservice.lambda.StreamLambdaHandler::handleRequest
```

## Lambda Settings (for all 3)

```
Runtime: Java 17
Architecture: x86_64
Timeout: 20 seconds
Memory: 512 MB
```

## API Gateway Routes (exact paths and methods)

| Method | Path                | Lambda Function     |
| ------ | ------------------- | ------------------- |
| POST   | /api/users          | arep-user-service   |
| PUT    | /api/users/username | arep-user-service   |
| GET    | /api/me             | arep-user-service   |
| POST   | /api/posts          | arep-posts-service  |
| GET    | /api/feed/public    | arep-stream-service |

## JAR File Locations (local, when uploading to Lambda)

```
User Service:   microservices/user-service/target/user-service-lambda.jar
Posts Service:  microservices/posts-service/target/posts-service-lambda.jar
Stream Service: microservices/stream-service/target/stream-service-lambda.jar
```

---

## Pre-Deployment Checklist

- [ ] Built all 3 jars successfully
- [ ] Neon project created with pooled endpoint
- [ ] Database schema loaded in Neon (V1\_\_init_schema.sql)
- [ ] Seed data inserted (V2\_\_seed_public_stream.sql)
- [ ] All values above filled in (no blank lines)
- [ ] Auth0 API created with correct identifier
- [ ] Auth0 SPA app created with correct client ID

## Deployment Checklist

- [ ] Created arep-user-service Lambda
- [ ] Created arep-posts-service Lambda
- [ ] Created arep-stream-service Lambda
- [ ] Configured environment variables on all 3 Lambdas
- [ ] Uploaded JARs to all 3 Lambdas
- [ ] Created API Gateway with all 5 routes
- [ ] Deployed API Gateway (got Invoke URL)
- [ ] Updated frontend .env with Invoke URL
- [ ] Updated Auth0 dashboard Callback URLs / Web Origins
- [ ] Built frontend (npm run build)
- [ ] Deployed frontend to S3

## Testing Checklist

- [ ] GET /api/feed/public returns 200 (no token required)
- [ ] Login on frontend succeeds
- [ ] POST /api/users returns 200 (with token)
- [ ] GET /api/me returns 200 (with token)
- [ ] POST /api/posts returns 200 (with token, scope write:posts)
- [ ] GET /api/feed/public returns your new post
