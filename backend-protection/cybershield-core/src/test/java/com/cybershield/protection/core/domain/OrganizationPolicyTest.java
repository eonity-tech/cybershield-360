package com.cybershield.protection.core.domain;

import org.junit.jupiter.api.Test;
import java.time.LocalTime;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrganizationPolicyTest {

    @Test
    void should_calculate_perfect_score_when_all_trained_and_few_admins() {
        // GIVEN : 100 employés, 100 formés, 5 admins (5% -> Pas de pénalité)
        OrganizationPolicy policy = new OrganizationPolicy(
                UUID.randomUUID(), "CyberCorp", 100, 100, 5,
                LocalTime.of(9, 0), LocalTime.of(18, 0), Collections.emptyList()
        );

        // WHEN
        double score = policy.calculateHumanMaturityScore();

        // THEN
        assertEquals(100.0, score);
    }

    @Test
    void should_apply_penalty_when_too_many_admins() {
        // GIVEN : 100 employés, 100 formés, MAIS 20 admins (20% -> Pénalité de 20 points)
        OrganizationPolicy policy = new OrganizationPolicy(
                UUID.randomUUID(), "InsecureCorp", 100, 100, 20,
                LocalTime.of(9, 0), LocalTime.of(18, 0), Collections.emptyList()
        );

        // WHEN
        double score = policy.calculateHumanMaturityScore();

        // THEN : 100 (Base) - 20 (Pénalité) = 80
        assertEquals(80.0, score);
    }

    @Test
    void should_detect_access_outside_working_hours() {
        OrganizationPolicy policy = new OrganizationPolicy(
                UUID.randomUUID(), "WorkLifeCorp", 10, 5, 1,
                LocalTime.of(0, 0), LocalTime.of(20, 0), Collections.emptyList() // Fin à 20h
        );

        // Cas OK : 14h00
        assertFalse(policy.isOutsideWorkingHours(LocalTime.of(14, 0)));

        // Cas KO : 23h00 (C'est après 20h)
        assertTrue(policy.isOutsideWorkingHours(LocalTime.of(23, 0)));
    }
}