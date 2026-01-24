package com.cybershield.protection.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class ApiKeyWebFilter implements WebFilter {

    @Value("${cybershield.api.key}")
    private String validApiKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // 1. On ne filtre que les appels à /api/v1/sync
        if (!path.startsWith("/api/v1/sync")) {
            return chain.filter(exchange);
        }

        // 2. Vérification du Header
        String requestApiKey = request.getHeaders().getFirst("X-API-KEY");

        if (validApiKey.equals(requestApiKey)) {
            // Clé
            return chain.filter(exchange);
        }

        // Clé invalide : On coupe le flux et on renvoie une erreur JSON
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String errorBody = "{\"status\": 401, \"error\": \"Unauthorized\", \"message\": \"Clé API invalide ou manquante (WebFlux)\"}";
        byte[] bytes = errorBody.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);

        return response.writeWith(Mono.just(buffer));
    }
}