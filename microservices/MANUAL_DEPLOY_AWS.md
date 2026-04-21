# Manual AWS Deployment Guide - Step by Step

This is the traditional manual deployment approach (no YAML/SAM).

---

## PHASE 1: Build Jars Locally

Run these commands from the repo root:

### Build User Service

```powershell
cd microservices\user-service
..\..\monolith\mvnw.cmd -DskipTests package
```

Expected result: `user-service/target/user-service-lambda.jar` created.

### Build Posts Service

```powershell
cd ..\posts-service
..\..\monolith\mvnw.cmd -DskipTests package
```

Expected result: `posts-service/target/posts-service-lambda.jar` created.

### Build Stream Service

```powershell
cd ..\stream-service
..\..\monolith\mvnw.cmd -DskipTests package
```

Expected result: `stream-service/target/stream-service-lambda.jar` created.

---

## PHASE 2: Create Lambda Functions in AWS Console

### Prepare: Get your values from Neon

Before creating Lambdas, copy from Neon:

- **Pooled Host:** `ep-xxxxxx-pooler.c-5.us-east-1.aws.neon.tech`
- **Database:** `neondb`
- **User:** `neondb_owner`
- **Password:** Your new password (already reset)

Also have Auth0 ready:

- **AUTH0_DOMAIN:** `your-tenant.auth0.com`
- **AUTH0_AUDIENCE:** `https://your-api-identifier` (from Auth0 API settings)

Your frontend URL (for CORS):

- **CORS_ALLOWED_ORIGIN:** `https://your-frontend-domain.s3.amazonaws.com` or CloudFront URL

---

### Create Lambda: arep-user-service

1. Open [AWS Console Lambda](https://console.aws.amazon.com/lambda/home)
2. Click **Create function**
3. Fill in:
   - **Function name:** `arep-user-service`
   - **Runtime:** Java 17
   - **Architecture:** x86_64
4. Click **Create function**
5. Wait for function to appear.

#### Configure handler

1. Go to **Configuration** tab
2. Click **General configuration** (if not visible, go to **Code** tab first)
3. Under **Handler**, enter:
   ```
   edu.escuelaing.arep.userservice.lambda.UserLambdaHandler::handleRequest
   ```
4. **Timeout:** Set to 20 seconds
5. **Memory:** Set to 512 MB
6. Click **Save**

#### Upload jar

1. Still in **Code** section, click **Upload from** → **Upload a file**
2. Select: `microservices/user-service/target/user-service-lambda.jar`
3. Wait for upload to complete.
4. Click **Save**

---

### Create Lambda: arep-posts-service

Repeat the same steps as user-service:

1. **Function name:** `arep-posts-service`
2. **Runtime:** Java 17
3. **Handler:** `edu.escuelaing.arep.postsservice.lambda.PostsLambdaHandler::handleRequest`
4. **Timeout:** 20 seconds
5. **Memory:** 512 MB
6. Upload: `microservices/posts-service/target/posts-service-lambda.jar`

---

### Create Lambda: arep-stream-service

Repeat again:

1. **Function name:** `arep-stream-service`
2. **Runtime:** Java 17
3. **Handler:** `edu.escuelaing.arep.streamservice.lambda.StreamLambdaHandler::handleRequest`
4. **Timeout:** 20 seconds
5. **Memory:** 512 MB
6. Upload: `microservices/stream-service/target/stream-service-lambda.jar`

---

## PHASE 3: Configure Environment Variables

For each of the 3 Lambdas, add these env vars:

### Environment Variables Values

Replace with your actual values:

| Key                 | Value                                                  | Example                                                                              |
| ------------------- | ------------------------------------------------------ | ------------------------------------------------------------------------------------ |
| DB_URL              | jdbc:postgresql://[POOLED_HOST]/neondb?sslmode=require | jdbc:postgresql://ep-xxxxx-pooler.c-5.us-east-1.aws.neon.tech/neondb?sslmode=require |
| DB_USERNAME         | neondb_owner                                           | neondb_owner                                                                         |
| DB_PASSWORD         | [YOUR_NEON_PASSWORD]                                   | (copy from Neon)                                                                     |
| AUTH0_DOMAIN        | [TENANT].auth0.com                                     | company.auth0.com                                                                    |
| AUTH0_AUDIENCE      | https://your-api-id                                    | https://arep-api                                                                     |
| CORS_ALLOWED_ORIGIN | Your frontend URL                                      | https://my-frontend.s3.amazonaws.com                                                 |

### For each Lambda (user, posts, stream):

1. Open the Lambda function
2. Go to **Configuration** tab
3. Click **Environment variables** on the left
4. Click **Edit**
5. Add each variable:
   - Key: `DB_URL`
   - Value: `jdbc:postgresql://ep-lingering-sunset-amx9eghn-pooler.c-5.us-east-1.aws.neon.tech/neondb?sslmode=require`
6. Click **Add environment variable** and repeat for:
   - `DB_USERNAME`
   - `DB_PASSWORD`
   - `AUTH0_DOMAIN`
   - `AUTH0_AUDIENCE`
   - `CORS_ALLOWED_ORIGIN`

7. Click **Save**

**Repeat for all 3 Lambdas** (arep-user-service, arep-posts-service, arep-stream-service).

---

## PHASE 4: Create API Gateway

### Create the API

1. Open [AWS Console API Gateway](https://console.aws.amazon.com/apigateway/home)
2. Click **Create API** → **REST API**
3. **API name:** `arep-api`
4. **Description:** "AREP microservices gateway"
5. Click **Create API**
6. You'll see a root resource `/`

### Create Routes

For each route below, repeat this:

1. Select the `/` resource
2. Click **Create resource**
3. **Resource name:** (see table below)
4. Click **Create resource**
5. Select the new resource
6. Click **Create method**
7. **Method:** (see table below)
8. **Integration type:** Lambda function
9. **Lambda function:** (see table below)
10. Click **Create method**

#### Route Table

| Method | Path                | Lambda              | Notes                    |
| ------ | ------------------- | ------------------- | ------------------------ |
| POST   | /api/users          | arep-user-service   | Creates/returns user     |
| PUT    | /api/users/username | arep-user-service   | Updates username         |
| GET    | /api/me             | arep-user-service   | Get current user profile |
| POST   | /api/posts          | arep-posts-service  | Create post              |
| GET    | /api/feed/public    | arep-stream-service | Get public feed          |

**Important:** Build the path tree correctly:

- First create `/api` resource
- Then create `/users`, `/posts`, `/feed` under `/api`
- Then create `/username` under `/api/users` and `/public` under `/api/feed`

### Enable CORS

1. Select the root `/` resource
2. Click **Actions** dropdown → **Enable CORS**
3. **Access-Control-Allow-Headers:** Keep default or add: `Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token`
4. Click **Enable CORS and replace existing CORS headers**
5. Deploy (see below)

### Deploy API

1. Click **Deploy API** or **Actions** → **Deploy API**
2. **Stage:** Create new: `prod`
3. Click **Deploy**
4. You'll get an **Invoke URL**, copy it (e.g., `https://xxxxx.execute-api.us-east-1.amazonaws.com/prod`)

---

## PHASE 5: Update Frontend Config

### Get the Invoke URL

From API Gateway (after deploy), copy the Invoke URL.

### Update Frontend .env

In `frontend/.env` (or `.env.local`):

```
VITE_API_URL=https://xxxxx.execute-api.us-east-1.amazonaws.com/prod
VITE_AUTH0_DOMAIN=your-tenant.auth0.com
VITE_AUTH0_CLIENT_ID=your-client-id
VITE_AUTH0_AUDIENCE=https://your-api-id
```

### Build and Deploy Frontend

```powershell
cd frontend
npm run build
# Upload dist/ to S3 bucket
```

---

## PHASE 6: Validate Auth0 Settings

In your Auth0 dashboard:

1. Go to **Applications** → **Applications** (find your SPA app)
2. Under **Settings**, update:
   - **Allowed Callback URLs:** `https://your-frontend-url/callback`
   - **Allowed Logout URLs:** `https://your-frontend-url/`
   - **Allowed Web Origins:** `https://your-frontend-url`

---

## PHASE 7: Quick Validation Tests

### Test 1: Public endpoint (no auth)

```bash
curl https://xxxxx.execute-api.us-east-1.amazonaws.com/prod/api/feed/public
```

Expected: 200 OK with array of posts (empty if no data yet).

### Test 2: Create user (with token)

1. Login in your frontend (Auth0 will give you a token).
2. In browser console:

```javascript
fetch("https://xxxxx.execute-api.us-east-1.amazonaws.com/prod/api/users", {
  method: "POST",
  headers: {
    Authorization: "Bearer " + YOUR_TOKEN,
    "Content-Type": "application/json",
  },
  body: JSON.stringify({}),
})
  .then((r) => r.json())
  .then(console.log);
```

Expected: 200 with user object.

### Test 3: Get profile

```javascript
fetch("https://xxxxx.execute-api.us-east-1.amazonaws.com/prod/api/me", {
  method: "GET",
  headers: {
    Authorization: "Bearer " + YOUR_TOKEN,
  },
})
  .then((r) => r.json())
  .then(console.log);
```

Expected: 200 with current user profile.

### Test 4: Create post

```javascript
fetch("https://xxxxx.execute-api.us-east-1.amazonaws.com/prod/api/posts", {
  method: "POST",
  headers: {
    Authorization: "Bearer " + YOUR_TOKEN,
    "Content-Type": "application/json",
  },
  body: JSON.stringify({ content: "Hello world" }),
})
  .then((r) => r.json())
  .then(console.log);
```

Expected: 200 with post ID.

### Test 5: Get feed with new post

```bash
curl https://xxxxx.execute-api.us-east-1.amazonaws.com/prod/api/feed/public
```

Expected: 200 with your post in the array.

---

## Troubleshooting

| Issue           | Check                                                       |
| --------------- | ----------------------------------------------------------- |
| 502 Bad Gateway | Handler class name correct? JAR uploaded?                   |
| Timeout error   | DB_URL reachable from Lambda? Auth0 responding?             |
| 403 Forbidden   | CORS_ALLOWED_ORIGIN matches frontend URL?                   |
| Empty feed      | Data in Neon? Seed data inserted?                           |
| Auth fails      | AUTH0_DOMAIN and AUTH0_AUDIENCE correct? Token scope valid? |

### Check Lambda Logs

1. Open Lambda function
2. Go to **Monitor** tab → **Logs**
3. Click latest **Log stream**
4. Look for error messages.
