package edu.escuelaing.arep.userservice.lambda;

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
import java.util.UUID;
import java.util.stream.Collectors;

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

public class UserLambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
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

    record CreateUserRequest(String email) {
    }

    record UpdateUsernameRequest(String username) {
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        try {
            if ("OPTIONS".equalsIgnoreCase(event.getHttpMethod())) {
                return response(204, null);
            }

            String method = event.getHttpMethod();
            String path = path(event);

            if ("POST".equalsIgnoreCase(method) && path.endsWith("/api/users")) {
                return createUser(event);
            }

            if ("PUT".equalsIgnoreCase(method) && path.endsWith("/api/users/username")) {
                return updateUsername(event);
            }

            if ("GET".equalsIgnoreCase(method) && path.endsWith("/api/me")) {
                return me(event);
            }

            throw new ApiException(404, "NOT_FOUND", "Route not found");
        } catch (ApiException ex) {
            return error(ex.status, ex.code, ex.getMessage());
        } catch (Exception ex) {
            return error(500, "INTERNAL_SERVER_ERROR", ex.getMessage());
        }
    }

    private APIGatewayProxyResponseEvent createUser(APIGatewayProxyRequestEvent event) throws Exception {
        String auth0Id = subjectFromBearer(header(event, "Authorization"));
        CreateUserRequest request = parse(event.getBody(), CreateUserRequest.class);

        if (request.email() == null || request.email().isBlank()) {
            throw new ApiException(400, "VALIDATION_ERROR", "email is required");
        }

        try (Connection conn = open()) {
            Map<String, Object> existing = findByAuth0(conn, auth0Id);
            if (existing != null) {
                return response(201, existing);
            }

            if (emailExists(conn, request.email())) {
                throw new ApiException(409, "EMAIL_ALREADY_EXISTS", "Email already exists");
            }

            String username = request.email().split("@")[0];
            Map<String, Object> created = insert(conn, auth0Id, username, request.email());
            return response(201, created);
        }
    }

    private APIGatewayProxyResponseEvent updateUsername(APIGatewayProxyRequestEvent event) throws Exception {
        String auth0Id = subjectFromBearer(header(event, "Authorization"));
        UpdateUsernameRequest request = parse(event.getBody(), UpdateUsernameRequest.class);

        String username = request.username() == null ? "" : request.username().trim();
        if (username.isBlank()) {
            throw new ApiException(400, "VALIDATION_ERROR", "username is required");
        }

        try (Connection conn = open()) {
            Map<String, Object> current = findByAuth0(conn, auth0Id);
            if (current == null) {
                throw new ApiException(404, "USER_NOT_FOUND", "User not found");
            }

            if (usernameExistsForOther(conn, username, String.valueOf(current.get("id")))) {
                throw new ApiException(409, "USERNAME_ALREADY_EXISTS", "Username already exists");
            }

            Map<String, Object> updated = updateUsername(conn, String.valueOf(current.get("id")), username);
            return response(200, updated);
        }
    }

    private APIGatewayProxyResponseEvent me(APIGatewayProxyRequestEvent event) throws Exception {
        String auth0Id = subjectFromBearer(header(event, "Authorization"), "read:profile");
        try (Connection conn = open()) {
            Map<String, Object> user = findByAuth0(conn, auth0Id);
            if (user == null) {
                throw new ApiException(404, "USER_NOT_FOUND", "User not found");
            }
            return response(200, user);
        }
    }

    private static Connection open() {
        try {
            return DriverManager.getConnection(required("DB_URL"), required("DB_USERNAME"), required("DB_PASSWORD"));
        } catch (SQLException ex) {
            throw new ApiException(500, "DB_CONNECTION_ERROR", ex.getMessage());
        }
    }

    private static Map<String, Object> findByAuth0(Connection conn, String auth0Id) throws Exception {
        String sql = "select id, username, email from users where auth0_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, auth0Id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Map<String, Object> out = new LinkedHashMap<>();
                out.put("id", rs.getString("id"));
                out.put("username", rs.getString("username"));
                out.put("email", rs.getString("email"));
                return out;
            }
        }
    }

    private static boolean emailExists(Connection conn, String email) throws Exception {
        String sql = "select count(*) as c from users where email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt("c") > 0;
            }
        }
    }

    private static boolean usernameExistsForOther(Connection conn, String username, String id) throws Exception {
        String sql = "select count(*) as c from users where username = ? and id <> ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, id);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt("c") > 0;
            }
        }
    }

    private static Map<String, Object> insert(Connection conn, String auth0Id, String username, String email)
            throws Exception {
        String id = UUID.randomUUID().toString();
        String sql = "insert into users (id, auth0_id, username, email) values (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.setString(2, auth0Id);
            stmt.setString(3, username);
            stmt.setString(4, email);
            stmt.executeUpdate();
        }
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("id", id);
        out.put("username", username);
        out.put("email", email);
        return out;
    }

    private static Map<String, Object> updateUsername(Connection conn, String id, String username) throws Exception {
        String updateSql = "update users set username = ? where id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
            stmt.setString(1, username);
            stmt.setString(2, id);
            stmt.executeUpdate();
        }

        String readSql = "select id, username, email from users where id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(readSql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                Map<String, Object> out = new LinkedHashMap<>();
                out.put("id", rs.getString("id"));
                out.put("username", rs.getString("username"));
                out.put("email", rs.getString("email"));
                return out;
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
            Jwk jwk = JWK_PROVIDER.get(decoded.getKeyId());
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
            throw new ApiException(401, "UNAUTHORIZED", "Invalid token");
        }
    }

    private static Set<String> scopesFromToken(DecodedJWT token) {
        Claim permissions = token.getClaim("permissions");
        List<String> fromPermissions = permissions.isNull() ? Collections.emptyList()
                : permissions.asList(String.class);

        Claim scope = token.getClaim("scope");
        List<String> fromScope = scope.isNull() ? Collections.emptyList()
                : Arrays.stream(scope.asString().split(" ")).filter(s -> !s.isBlank()).toList();

        Claim scp = token.getClaim("scp");
        List<String> fromScp = scp.isNull() ? Collections.emptyList() : scp.asList(String.class);

        return List.of(fromPermissions, fromScope, fromScp).stream().flatMap(List::stream).collect(Collectors.toSet());
    }

    private static String path(APIGatewayProxyRequestEvent event) {
        if (event.getPath() == null || event.getPath().isBlank()) {
            return "/";
        }
        return event.getPath();
    }

    private static String header(APIGatewayProxyRequestEvent event, String key) {
        if (event.getHeaders() == null) {
            return null;
        }
        String value = event.getHeaders().get(key);
        if (value != null) {
            return value;
        }
        return event.getHeaders().get(key.toLowerCase());
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
