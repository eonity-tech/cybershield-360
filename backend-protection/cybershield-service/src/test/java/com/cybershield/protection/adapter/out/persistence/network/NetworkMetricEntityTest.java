package com.cybershield.protection.adapter.out.persistence.network;

import com.cybershield.protection.core.domain.NetworkMetric;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NetworkMetricEntityTest {

    @Test
    void shouldMapDomainToEntityAndBack() {
        // 1. GIVEN : Une métrique historique (pas "maintenant")
        UUID id = UUID.randomUUID();
        UUID deviceId = UUID.randomUUID();
        LocalDateTime historyDate = LocalDateTime.of(2022, 5, 20, 14, 30, 0);

        NetworkMetric originalDomain = new NetworkMetric(
                id, deviceId, 500.0, 1000.0, historyDate
        );

        // 2. WHEN : Conversion vers Entity
        NetworkMetricEntity entity = NetworkMetricEntity.fromDomain(originalDomain);

        // Vérifications intermédiaires
        assertEquals(id, entity.getId());
        assertEquals(500.0, entity.getBytesSent());
        assertEquals(historyDate, entity.getTimestamp()); // La date doit être dans l'entité

        // 3. WHEN : Retour vers Domaine
        NetworkMetric resultDomain = entity.toDomain();

        // 4. THEN : Vérification finale
        assertEquals(originalDomain.getId(), resultDomain.getId());
        assertEquals(originalDomain.getBytesSent(), resultDomain.getBytesSent());

        // Le test CRITIQUE : Est-ce que la date est restée 2022 ?
        // Si toDomain() est mal fait, ça vaudra "maintenant" et le test échouera.
        assertEquals(historyDate, resultDomain.getTimestamp());
    }
}