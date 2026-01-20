package com.cybershield.protection.adapter.in.rest.error;

import com.cybershield.protection.core.domain.CompliancePolicy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CompliancePolicy.ComplianceException.class)
    public ResponseEntity<Map<String, Object>> handleComplianceException(CompliancePolicy.ComplianceException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Security Policy Violation");
        body.put("message", ex.getMessage()); // Affiche "Sécurité : Les appareils avec un OS 'UNKNOWN' sont interdits."

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Gère les erreurs de validation du Domain (ex: MAC invalide dans le constructeur device)
     * Règle : IllegalArgumentException -> 400 Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleDomainValidation(IllegalArgumentException ex, ServerWebExchange exchange) {
        log.warn("Domain validation error on {}: {}", exchange.getRequest().getPath(), ex.getMessage());

        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.name(),
                ex.getMessage() // "Format d'adresse MAC invalide..."
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Gère les erreurs de validation Spring (@Valid dans le controller) pour WebFlux
     * Règle : WebExchangeBindException -> 400 Bad Request avec détails
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ApiError> handleSpringValidation(WebExchangeBindException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> "%s: %s".formatted(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.name(),
                "Validation failed for input data",
                details
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Gère toutes les autres erreurs non prévues (Catch-all)
     * Règle : Exception -> 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex, ServerWebExchange exchange) {
        log.error("Unexpected error on {}", exchange.getRequest().getPath(), ex);

        ApiError error = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                "An unexpected error occurred. Please contact support."
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Gère les conflits d'état (ex: device déjà enregistré)
     * Règle : IllegalStateException -> 409 Conflict
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleBusinessConflict(IllegalStateException ex, ServerWebExchange exchange) {
        log.warn("Business conflict on {}: {}", exchange.getRequest().getPath(), ex.getMessage());

        ApiError error = new ApiError(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.name(),
                ex.getMessage() // "L'appareil avec l'adresse MAC ... est déjà enregistré."
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Gère les accès refusés au niveau métier
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex, ServerWebExchange exchange) {
        log.warn("Access denied on {}: {}", exchange.getRequest().getPath(), ex.getMessage());

        ApiError error = new ApiError(
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.name(),
                "Vous n'avez pas les permissions nécessaires pour accéder à cette ressource."
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
}