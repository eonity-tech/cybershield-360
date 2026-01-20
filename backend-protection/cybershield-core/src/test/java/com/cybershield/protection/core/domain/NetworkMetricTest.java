package com.cybershield.protection.core.domain;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NetworkMetricTest {

    // --- SCÉNARIO 1 : Création d'une nouvelle métrique réseau ---
    @Test
    void shouldCreateNewMetricWithCurrentTimestamp() {
        // GIVEN
        UUID id = UUID.randomUUID();
        UUID deviceId = UUID.randomUUID();

        // WHEN (Constructeur court -> date automatique)
        NetworkMetric metric = new NetworkMetric(id, deviceId, 100.0, 200.0);

        // THEN
        assertNotNull(metric.getTimestamp(), "Le timestamp doit être généré");
        assertTrue(metric.getTimestamp().isAfter(LocalDateTime.now().minusSeconds(1)));

        assertEquals(id, metric.getId());
        assertEquals(deviceId, metric.getDeviceId());
    }

    // --- SCÉNARIO 2 : Reconstruction d'une métrique réseau historique ---
    @Test
    void shouldReconstructMetricFromHistory() {
        // GIVEN
        UUID id = UUID.randomUUID();
        UUID deviceId = UUID.randomUUID();

        LocalDateTime historicalDate = LocalDateTime.of(2023, 1, 1, 12, 0, 0);

        // WHEN (Constructeur long -> force la date)
        NetworkMetric metric = new NetworkMetric(id, deviceId, 500.0, 500.0, historicalDate);

        // THEN
        assertEquals(historicalDate, metric.getTimestamp(), "La date doit être celle fournie (historique)");
    }
}