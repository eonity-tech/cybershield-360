package com.cybershield.protection.adapter.out.persistence.software;

import com.cybershield.protection.core.domain.Software;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SoftwareEntityTest {

    // --- Scénario 1: Vérifier la conversion complète (avec le nouveau champ 'type') ---
    @Test
    void shouldMapDomainToEntityAndBack() {
        // 1. GIVEN : Un objet Software du Domaine
        UUID id = UUID.randomUUID();
        UUID deviceId = UUID.randomUUID();

        // MISE À JOUR : Ajout du paramètre "Application" (le type)
        Software domainSoftware = new Software(
                id,
                deviceId,
                "Adobe Acrobat",
                "22.0.1",
                "Application", // Nouveau champ 'type'
                "Adobe",
                true // isRunning
        );

        domainSoftware.setCriticalScore(7.5);

        // 2. WHEN : Conversion vers ENTITY
        SoftwareEntity entity = SoftwareEntity.fromDomain(domainSoftware);

        // 3. THEN : Vérification des données (y compris le type)
        assertEquals(id, entity.getId());
        assertEquals(deviceId, entity.getDeviceId());
        assertEquals("Adobe Acrobat", entity.getName());
        assertEquals("22.0.1", entity.getVersion());
        assertEquals("Application", entity.getType()); // ✅ Vérification du type
        assertEquals("Adobe", entity.getPublisher());
        assertTrue(entity.isRunning());
        assertEquals(7.5, entity.getCriticalScore());

        // 4. WHEN : Conversion retour vers DOMAINE
        Software resultDomain = entity.toDomain();

        // 5. THEN : On vérifie que tout est revenu à l'identique
        assertEquals(domainSoftware.getId(), resultDomain.getId());
        assertEquals(domainSoftware.getName(), resultDomain.getName());
        assertEquals(domainSoftware.getType(), resultDomain.getType()); // ✅ Vérification retour
        assertEquals(domainSoftware.isRunning(), resultDomain.isRunning());
    }

    // --- Scénario 2: Vérifier les getters et setters ---
    @Test
    void shouldHandleGettersAndSettersCorrectly() {
        SoftwareEntity entity = new SoftwareEntity();
        UUID id = UUID.randomUUID();
        UUID deviceId = UUID.randomUUID();

        entity.setId(id);
        entity.setDeviceId(deviceId);
        entity.setName("Test App");
        entity.setVersion("1.0");
        entity.setType("Antivirus"); // ✅ Test du setter Type
        entity.setPublisher("Tester");
        entity.setRunning(false);
        entity.setCriticalScore(9.9);

        assertEquals(id, entity.getId());
        assertEquals(deviceId, entity.getDeviceId());
        assertEquals("Test App", entity.getName());
        assertEquals("1.0", entity.getVersion());
        assertEquals("Antivirus", entity.getType()); // ✅ Test du getter Type
        assertEquals("Tester", entity.getPublisher());
        assertFalse(entity.isRunning());
        assertEquals(9.9, entity.getCriticalScore());
    }
}