package com.accenture.booklibrary.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;
import java.util.List;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String ADMIN = "ADMIN";
    private static final String CLIENT = "CLIENT";

    @Value("${book.library.jwt.issuer-uri}")
    private String jwtIssuerUri;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ObjectMapper objectMapper) throws Exception {
        String[] securedGetEndpoints = {
                "/api/v1/books/title/**",
                "/api/v1/books/author/**",
                "/api/v1/books/categories",
                "/api/v1/books/titles",
                "/api/v1/books/authors"
        };
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new AuthRoleConverter());
        http
                .sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(corsConfig -> corsConfig.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
                    config.setAllowedMethods(Collections.singletonList("*"));
                    config.setAllowCredentials(true);
                    config.setAllowedHeaders(Collections.singletonList("*"));
                    config.setExposedHeaders(List.of("Authorization"));
                    config.setMaxAge(3600L);
                    return config;
                }))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toH2Console()).permitAll()
                        .requestMatchers(POST, "/api/v1/books").hasRole(ADMIN)
                        .requestMatchers(DELETE, "/api/v1/books/**").hasRole(ADMIN)
                        .requestMatchers(GET, securedGetEndpoints).hasAnyRole(ADMIN, CLIENT)
                        .anyRequest().authenticated())
                .csrf(csrf -> csrf.ignoringRequestMatchers(PathRequest.toH2Console()))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter)))
                .exceptionHandling(ehc -> ehc.accessDeniedHandler(new CustomAccessDeniedHandler(objectMapper)));
        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(jwtIssuerUri).build();
    }
}
