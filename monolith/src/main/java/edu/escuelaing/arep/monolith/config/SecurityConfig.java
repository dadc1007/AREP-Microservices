package edu.escuelaing.arep.monolith.config;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import edu.escuelaing.arep.monolith.dto.response.ErrorResponse;
import edu.escuelaing.arep.monolith.exception.SecurityErrorCode;

@Configuration
public class SecurityConfig {
        @Bean
        SecurityFilterChain apiSecurity(HttpSecurity http, AuthenticationEntryPoint authenticationEntryPoint,
                        AccessDeniedHandler accessDeniedHandler) throws Exception {
                return http.csrf(csrf -> csrf.disable()).cors(Customizer.withDefaults())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .exceptionHandling(exceptions -> exceptions
                                                .authenticationEntryPoint(authenticationEntryPoint)
                                                .accessDeniedHandler(accessDeniedHandler))
                                .authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.OPTIONS, "/**")
                                                .permitAll()
                                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**",
                                                                "/swagger-ui.html")
                                                .permitAll().requestMatchers(HttpMethod.GET, "/api/feed/public")
                                                .permitAll().requestMatchers(HttpMethod.POST, "/api/posts")
                                                .hasAuthority("SCOPE_write:posts")
                                                .requestMatchers(HttpMethod.GET, "/api/me")
                                                .hasAuthority("SCOPE_read:profile").anyRequest().authenticated())
                                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
                                                .jwtAuthenticationConverter(jwtAuthenticationConverter())))
                                .build();
        }

        @Bean
        JwtAuthenticationConverter jwtAuthenticationConverter() {
                JwtGrantedAuthoritiesConverter permissionsConverter = new JwtGrantedAuthoritiesConverter();
                permissionsConverter.setAuthoritiesClaimName("permissions");
                permissionsConverter.setAuthorityPrefix("SCOPE_");

                JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
                jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(permissionsConverter);
                return jwtAuthenticationConverter;
        }

        @Bean
        CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOriginPatterns(List.of("http://localhost:5173"));
                configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(List.of("*"));

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }

        @Bean
        AuthenticationEntryPoint authenticationEntryPoint(ObjectMapper objectMapper) {
                return (request, response, authException) -> {
                        writeErrorResponse(response, objectMapper,
                                        SecurityErrorCode.UNAUTHORIZED, authException.getMessage());
                };
        }

        @Bean
        AccessDeniedHandler accessDeniedHandler(ObjectMapper objectMapper) {
                return (request, response, accessDeniedException) -> {
                        writeErrorResponse(response, objectMapper,
                                        SecurityErrorCode.FORBIDDEN, accessDeniedException.getMessage());
                };
        }

        private void writeErrorResponse(HttpServletResponse response, ObjectMapper objectMapper,
                        SecurityErrorCode errorCode, String message) throws IOException {
                response.setStatus(errorCode.getHttpStatus().value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(errorCode.getHttpStatus().value())
                                .error(errorCode.name())
                                .message(message != null ? message : errorCode.getMessage())
                                .timestamp(LocalDateTime.now())
                                .build();

                objectMapper.writeValue(response.getWriter(), errorResponse);
        }
}
