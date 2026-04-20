package edu.escuelaing.arep.streamservice.lambda;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StreamLambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        try {
            if ("OPTIONS".equalsIgnoreCase(event.getHttpMethod())) {
                return response(204, null);
            }

            String method = event.getHttpMethod();
            String path = path(event);

            if ("GET".equalsIgnoreCase(method) && path.endsWith("/api/feed/public")) {
                return getPublicFeed();
            }

            throw new ApiException(404, "NOT_FOUND", "Route not found");
        } catch (ApiException ex) {
            return error(ex.status, ex.code, ex.getMessage());
        } catch (Exception ex) {
            return error(500, "INTERNAL_SERVER_ERROR", ex.getMessage());
        }
    }

    private APIGatewayProxyResponseEvent getPublicFeed() throws Exception {
        int limit = Integer.parseInt(optional("STREAM_DEFAULT_LIMIT", "100"));

        String sql = """
                select p.id, p.content, u.username
                from posts p
                join users u on u.id = p.user_id
                join streams s on s.id = p.stream_id
                where s.type = 'PUBLIC'
                order by p.id desc
                limit ?
                """;

        List<Map<String, Object>> posts = new ArrayList<>();

        try (Connection conn = open();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", rs.getLong("id"));
                    row.put("content", rs.getString("content"));
                    row.put("username", rs.getString("username"));
                    posts.add(row);
                }
            }
        }

        return response(200, posts);
    }

    private static Connection open() {
        try {
            return DriverManager.getConnection(required("DB_URL"), required("DB_USERNAME"), required("DB_PASSWORD"));
        } catch (SQLException ex) {
            throw new ApiException(500, "DB_CONNECTION_ERROR", ex.getMessage());
        }
    }

    private static String path(APIGatewayProxyRequestEvent event) {
        if (event.getPath() == null || event.getPath().isBlank()) {
            return "/";
        }
        return event.getPath();
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
