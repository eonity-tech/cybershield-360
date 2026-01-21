package com.cybershield.protection.core.usecase;

import com.cybershield.protection.core.domain.OrganizationPolicy;
import com.cybershield.protection.core.port.out.OrganizationPolicyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrganizationPolicyServiceTest {

    @Mock
    private OrganizationPolicyRepository repository;

    @InjectMocks
    private OrganizationPolicyService service;

    // scenario 1: définir une politique valide
    @Test
    void should_save_valid_policy() {
        // WHEN
        service.definePolicy(
                "MyCorp", 100, 50, 2,
                LocalTime.of(8, 0), LocalTime.of(18, 0), List.of("00:11:22:33:44:55")
        );

        // THEN : On vérifie que le repository a bien été appelé pour sauvegarder la politique
        verify(repository).save(any(OrganizationPolicy.class));
    }

    // scenario 2: définir une politique avec plus de formés que d'employés
    @Test
    void should_throw_exception_when_more_trained_than_employees() {
        // WHEN & THEN
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.definePolicy(
                    "MyCorp",
                    50,  // Total
                    51,  // Formés (Impossible !)
                    2, LocalTime.of(8, 0), LocalTime.of(18, 0), List.of()
            );
        });

        assertEquals("Il ne peut pas y avoir plus de formés que d'employés !", exception.getMessage());
    }
}