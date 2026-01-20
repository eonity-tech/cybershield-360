package com.cybershield.protection.adapter.infrastructure.jwt;

import java.util.List;
import java.util.UUID;

public record ConnectedUser(
        UUID id,            // L'ID technique de l'utilisateur (sub)
        String username,    // Email ou login
        List<String> roles  // Rôles (admin, user, etc.)
) {}