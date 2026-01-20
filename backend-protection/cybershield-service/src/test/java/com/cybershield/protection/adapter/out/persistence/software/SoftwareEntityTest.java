package com.cybershield.protection.adapter.out.persistence.software;

import com.cybershield.protection.core.domain.Software;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SoftwareEntityTest {

    // --- scénario 1: Vérifier la conversion entre Software (Domaine) et SoftwareEntity (Entité JPA) ---
    @Test
    void shouldMapDomainToEntityAndBack() {
        // 1. GIVEN : Un objet Software du Domaine
        UUID id = UUID.randomUUID();
        UUID deviceId = UUID.randomUUID();

        Software domainSoftware = new Software(
                id,
                deviceId,
                "Adobe Acrobat",
                "22.0.1",
                "Adobe",
                true // isRunning
        );
        // On définit un score critique (si ton objet Domain le permet via setter)
        // Sinon, assure-toi que ton entité gère le null ou une valeur par défaut
        domainSoftware.setCriticalScore(7.5);

        // 2. WHEN : Conversion vers ENTITY (Test de fromDomain + Setters)
        SoftwareEntity entity = SoftwareEntity.fromDomain(domainSoftware);

        // 3. THEN : Vérification que l'entité contient bien les données
        assertEquals(id, entity.getId());
        assertEquals(deviceId, entity.getDeviceId());
        assertEquals("Adobe Acrobat", entity.getName());
        assertEquals("22.0.1", entity.getVersion());
        assertEquals("Adobe", entity.getPublisher());
        assertTrue(entity.isRunning());
        assertEquals(7.5, entity.getCriticalScore());

        // 4. WHEN : Conversion retour vers DOMAINE (Test de toDomain + Getters)
        Software resultDomain = entity.toDomain();

        // 5. THEN : On vérifie que l'objet final est identique à l'original
        assertEquals(domainSoftware.getId(), resultDomain.getId());
        assertEquals(domainSoftware.getName(), resultDomain.getName());
        assertEquals(domainSoftware.isRunning(), resultDomain.isRunning());
    }

    // --- scénario 2: Vérifier les getters et setters de SoftwareEntity ---
    @Test
    void shouldHandleGettersAndSettersCorrectly() {
        SoftwareEntity entity = new SoftwareEntity();
        UUID id = UUID.randomUUID();
        UUID deviceId = UUID.randomUUID();

        entity.setId(id);
        entity.setDeviceId(deviceId);
        entity.setName("Test App");
        entity.setVersion("1.0");
        entity.setPublisher("Tester");
        entity.setRunning(false);
        entity.setCriticalScore(9.9);

        assertEquals(id, entity.getId());
        assertEquals(deviceId, entity.getDeviceId());
        assertEquals("Test App", entity.getName());
        assertEquals("1.0", entity.getVersion());
        assertEquals("Tester", entity.getPublisher());
        assertFalse(entity.isRunning());
        assertEquals(9.9, entity.getCriticalScore());
    }
}