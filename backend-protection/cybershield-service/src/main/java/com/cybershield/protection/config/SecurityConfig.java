package com.cybershield.protection.config;

import com.cybershield.protection.config.security.ApiKeyWebFilter; // Import du filtre
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder; // Pour l'ordre
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    // On injecte le filtre API Key
    private final ApiKeyWebFilter apiKeyWebFilter;

    public SecurityConfig(ApiKeyWebFilter apiKeyWebFilter) {
        this.apiKeyWebFilter = apiKeyWebFilter;
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                // --- INSERTION DU FILTRE API KEY ---
                // AVANT l'authentification classique
                .addFilterBefore(apiKeyWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)

                .authorizeExchange(exchanges -> exchanges
                        // A. LA SONDE (API Key) -> On met permitAll car le filtre gère le rejet
                        .pathMatchers("/api/v1/sync/**").permitAll()

                        // B. LISTE BLANCHE (Swagger, Actuator, Dashboard public)
                        .pathMatchers("/actuator/**", "/webjars/**", "/v3/api-docs/**").permitAll()
                        .pathMatchers("/api/v1/network-monitoring/dashboard").permitAll()

                        // C. LE RESTE (Keycloak / Devices / Admin)
                        .anyExchange().authenticated()
                )
                // gestion des erreurs d'authentification
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((exchange, e) -> {
                            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                            exchange.getResponse().getHeaders().setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
                            String body = "{\"status\": 401, \"error\": \"Unauthorized\", \"message\": \"Jeton JWT Keycloak invalide ou manquant\"}";
                            return exchange.getResponse().writeWith(reactor.core.publisher.Mono.just(
                                    exchange.getResponse().bufferFactory().wrap(body.getBytes())
                            ));
                        })
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(org.springframework.security.config.Customizer.withDefaults()));

        return http.build();
    }
}