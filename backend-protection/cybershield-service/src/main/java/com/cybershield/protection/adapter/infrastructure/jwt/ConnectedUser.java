package com.cybershield.protection.adapter.infrastructure.jwt;

import java.util.List;
import java.util.UUID;

public record ConnectedUser(
        UUID id,            // L'ID technique de l'utilisateur (sub)
        String username,    // Son email ou login
        List<String> roles  // Ses rôles (admin, user, etc.)
) {}