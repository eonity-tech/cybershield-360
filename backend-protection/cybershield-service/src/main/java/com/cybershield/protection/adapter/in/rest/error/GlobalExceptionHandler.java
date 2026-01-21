package com.cybershield.protection.adapter.in.rest.error;

import com.cybershield.protection.core.domain.CompliancePolicy;
import com.cybershield.protection.core.exception.PolicyAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 3. DÉCLARATION MANUELLE DU LOGGER (Infaillible)
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Gère les violations de politique de sécurité (Ex: OS interdit)
     */
    @ExceptionHandler(CompliancePolicy.ComplianceException.class)
    public ResponseEntity<Map<String, Object>> handleComplianceException(CompliancePolicy.ComplianceException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Security Policy Violation");
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Gère les erreurs de validation métier (Ex: Format MAC invalide)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleDomainValidation(IllegalArgumentException ex, ServerWebExchange exchange) {
        // Le "log" fonctionne maintenant car on l'a déclaré en haut
        log.warn("Domain validation error on {}: {}", exchange.getRequest().getPath(), ex.getMessage());

        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.name(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Gère les erreurs de validation Spring (@Valid)
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
     * Gère les erreurs techniques imprévues (500)
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
     * Gère les conflits (Ex: Device déjà enregistré)
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleBusinessConflict(IllegalStateException ex, ServerWebExchange exchange) {
        log.warn("Business conflict on {}: {}", exchange.getRequest().getPath(), ex.getMessage());

        ApiError error = new ApiError(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.name(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Gère les accès interdits (Ex: Token invalide ou permissions insuffisantes)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex, ServerWebExchange exchange) {
        log.warn("Access denied on {}: {}", exchange.getRequest().getPath(), ex.getMessage());

        ApiError error = new ApiError(
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.name(),
                "Vous n'avez pas les permissions nécessaires pour accéder à cette ressource."
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    /**
     * Gère les tentatives de création de politique déjà existante
     */
    @ExceptionHandler(PolicyAlreadyExistsException.class)
    public ResponseEntity<ApiError> handlePolicyAlreadyExists(PolicyAlreadyExistsException ex) {
        ApiError error = new ApiError(
                HttpStatus.CONFLICT.value(),
                "Data Conflict", // Un code d'erreur générique
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
}