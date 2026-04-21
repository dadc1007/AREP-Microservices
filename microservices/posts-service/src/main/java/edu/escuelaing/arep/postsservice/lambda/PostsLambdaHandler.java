package edu.escuelaing.arep.postsservice.lambda;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PostsLambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String AUTH0_DOMAIN = required("AUTH0_DOMAIN");
    private static final String AUTH0_AUDIENCE = required("AUTH0_AUDIENCE");
    private static final String ISSUER = "https://" + AUTH0_DOMAIN + "/";
    private static final JwkProvider JWK_PROVIDER;

    static {
        try {
            JWK_PROVIDER = new JwkProviderBuilder(new URL(ISSUER + ".well-known/jwks.json")).build();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    record CreatePostRequest(String content) {
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        try {
            if ("OPTIONS".equalsIgnoreCase(event.getHttpMethod())) {
                return response(204, null);
            }

            String method = event.getHttpMethod();
            String path = path(event);

            if ("POST".equalsIgnoreCase(method) && isCreatePostPath(path)) {
                return createPost(event);
            }

            throw new ApiException(404, "NOT_FOUND", "Route not found");
        } catch (ApiException ex) {
            return error(ex.status, ex.code, ex.getMessage());
        } catch (Exception ex) {
            return error(500, "INTERNAL_SERVER_ERROR", ex.getMessage());
        }
    }

    private APIGatewayProxyResponseEvent createPost(APIGatewayProxyRequestEvent event) throws Exception {
        String auth0Id = subjectFromBearer(header(event, "Authorization"), "write:posts");
        CreatePostRequest request = parse(event.getBody(), CreatePostRequest.class);

        String content = request.content() == null ? "" : request.content().trim();
        if (content.isBlank()) {
            throw new ApiException(400, "POST_EMPTY", "Post content is required");
        }

        if (content.length() > 140) {
            throw new ApiException(400, "POST_TOO_LONG", "Post cannot exceed 140 characters");
        }

        try (Connection conn = open()) {
            String userId = findUserId(conn, auth0Id);
            if (userId == null) {
                throw new ApiException(404, "USER_NOT_FOUND", "User not found");
            }

            Long streamId = findPublicStreamId(conn);
            if (streamId == null) {
                throw new ApiException(404, "STREAM_NOT_FOUND", "Public stream not found");
            }

            String username = findUsername(conn, userId);
            Long postId = insertPost(conn, content, userId, streamId);

            Map<String, Object> out = new LinkedHashMap<>();
            out.put("id", postId);
            out.put("content", content);
            out.put("username", username);

            return response(201, out);
        }
    }

    private static Connection open() {
        try {
            return DriverManager.getConnection(required("DB_URL"), required("DB_USERNAME"), required("DB_PASSWORD"));
        } catch (SQLException ex) {
            throw new ApiException(500, "DB_CONNECTION_ERROR", ex.getMessage());
        }
    }

    private static String findUserId(Connection conn, String auth0Id) throws Exception {
        String sql = "select id from users where auth0_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, auth0Id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return rs.getString("id");
            }
        }
    }

    private static Long findPublicStreamId(Connection conn) throws Exception {
        String sql = "select id from streams where type = 'PUBLIC'";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            if (!rs.next()) {
                return null;
            }
            return rs.getLong("id");
        }
    }

    private static String findUsername(Connection conn, String userId) throws Exception {
        String sql = "select username from users where id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return rs.getString("username");
            }
        }
    }

    private static Long insertPost(Connection conn, String content, String userId, Long streamId) throws Exception {
        String sql = "insert into posts (content, user_id, stream_id) values (?, ?, ?) returning id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, content);
            stmt.setString(2, userId);
            stmt.setLong(3, streamId);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getLong("id");
            }
        }
    }

    private static String subjectFromBearer(String authorizationHeader, String... requiredScopes) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ApiException(401, "UNAUTHORIZED", "Missing bearer token");
        }

        String token = authorizationHeader.substring(7).trim();
        if (token.isBlank()) {
            throw new ApiException(401, "UNAUTHORIZED", "Missing bearer token");
        }

        try {
            DecodedJWT decoded = JWT.decode(token);
            String keyId = decoded.getKeyId();
            if (keyId == null || keyId.isBlank()) {
                throw new ApiException(401, "UNAUTHORIZED", "Invalid token");
            }

            Jwk jwk = JWK_PROVIDER.get(keyId);
            if (jwk == null || jwk.getPublicKey() == null) {
                throw new ApiException(401, "UNAUTHORIZED", "Invalid token");
            }

            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .withAudience(AUTH0_AUDIENCE)
                    .build();
            DecodedJWT verified = verifier.verify(token);

            Set<String> scopes = scopesFromToken(verified);
            for (String scope : requiredScopes) {
                if (!scopes.contains(scope)) {
                    throw new ApiException(403, "FORBIDDEN", "Missing scope: " + scope);
                }
            }

            return verified.getSubject();
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            System.err.println("JWT validation failed: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
            throw new ApiException(401, "UNAUTHORIZED", "Invalid token");
        }
    }

    private static Set<String> scopesFromToken(DecodedJWT token) {
        Claim permissions = token.getClaim("permissions");
        List<String> fromPermissions = permissions.isNull() ? Collections.emptyList()
                : safeList(permissions.asList(String.class));

        Claim scope = token.getClaim("scope");
        String scopeValue = scope.isNull() ? null : scope.asString();
        List<String> fromScope = (scopeValue == null || scopeValue.isBlank()) ? Collections.emptyList()
                : Arrays.stream(scopeValue.split(" ")).filter(s -> !s.isBlank()).toList();

        Claim scp = token.getClaim("scp");
        List<String> fromScp = scp.isNull() ? Collections.emptyList() : safeList(scp.asList(String.class));

        return Stream.of(fromPermissions, fromScope, fromScp).flatMap(List::stream).collect(Collectors.toSet());
    }

    private static List<String> safeList(List<String> values) {
        return values == null ? Collections.emptyList() : values;
    }

    private static String path(APIGatewayProxyRequestEvent event) {
        if (event.getPath() == null || event.getPath().isBlank()) {
            return "/";
        }
        return event.getPath();
    }

    private static boolean isCreatePostPath(String path) {
        String normalizedPath = path.endsWith("/") && path.length() > 1
                ? path.substring(0, path.length() - 1)
                : path;

        return normalizedPath.endsWith("/api/posts") || normalizedPath.endsWith("/posts");
    }

    private static String header(APIGatewayProxyRequestEvent event, String key) {
        if (event.getHeaders() != null) {
            String value = event.getHeaders().get(key);
            if (value != null && !value.isBlank()) {
                return value;
            }

            String lowerCaseValue = event.getHeaders().get(key.toLowerCase());
            if (lowerCaseValue != null && !lowerCaseValue.isBlank()) {
                return lowerCaseValue;
            }
        }

        if (event.getMultiValueHeaders() != null) {
            List<String> values = event.getMultiValueHeaders().get(key);
            if (values != null && !values.isEmpty() && values.get(0) != null && !values.get(0).isBlank()) {
                return values.get(0);
            }

            List<String> lowerCaseValues = event.getMultiValueHeaders().get(key.toLowerCase());
            if (lowerCaseValues != null && !lowerCaseValues.isEmpty() && lowerCaseValues.get(0) != null
                    && !lowerCaseValues.get(0).isBlank()) {
                return lowerCaseValues.get(0);
            }
        }

        return null;
    }

    private static <T> T parse(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (Exception ex) {
            throw new ApiException(400, "INVALID_JSON", "Invalid request body");
        }
    }

    private static APIGatewayProxyResponseEvent response(int status, Object body) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent().withStatusCode(status)
                .withHeaders(Map.of(
                        "Content-Type", "application/json",
                        "Access-Control-Allow-Origin", optional("CORS_ALLOWED_ORIGIN", "http://localhost:5173"),
                        "Access-Control-Allow-Headers", "Authorization,Content-Type",
                        "Access-Control-Allow-Methods", "GET,POST,PUT,OPTIONS"));

        if (body != null) {
            try {
                response.withBody(MAPPER.writeValueAsString(body));
            } catch (JsonProcessingException ex) {
                throw new ApiException(500, "JSON_ERROR", "Failed to serialize response");
            }
        }

        return response;
    }

    private static APIGatewayProxyResponseEvent error(int status, String code, String message) {
        Map<String, Object> payload = Map.of(
                "status", status,
                "error", code,
                "message", message == null ? "Unexpected error" : message,
                "timestamp", LocalDateTime.now().toString());
        return response(status, payload);
    }

    private static String required(String key) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            throw new ApiException(500, "ENV_MISSING", "Missing env var: " + key);
        }
        return value;
    }

    private static String optional(String key, String fallback) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value;
    }

    private static final class ApiException extends RuntimeException {
        private final int status;
        private final String code;

        private ApiException(int status, String code, String message) {
            super(message);
            this.status = status;
            this.code = code;
        }
    }
}
