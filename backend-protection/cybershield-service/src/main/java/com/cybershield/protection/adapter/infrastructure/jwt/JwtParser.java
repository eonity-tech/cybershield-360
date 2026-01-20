package com.cybershield.protection.adapter.infrastructure.jwt;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtParser {

    public ConnectedUser parse(Jwt jwt) {
        // 1. Récupération de l'ID (Subject).
        String subject = jwt.getSubject();
        UUID id = null;
        try {
            id = UUID.fromString(subject);
        } catch (IllegalArgumentException e) {
            // Si le sub n'est pas un UUID, on génère un UUID temporaire ou on gère l'erreur
        }

        // 2. Récupération du username (parfois 'email', parfois 'preferred_username')
        String username = jwt.getClaimAsString("preferred_username");
        if (username == null) {
            username = jwt.getClaimAsString("email");
        }
        if (username == null) {
            username = subject;
        }

        // 3. Récupération des rôles (Exemple standard Keycloak: realm_access.roles)
        List<String> roles = Collections.emptyList();
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            roles = (List<String>) realmAccess.get("roles");
        }

        return new ConnectedUser(id, username, roles);
    }
}