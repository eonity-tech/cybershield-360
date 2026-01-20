package com.cybershield.protection.adapter.out.persistence.device;

import com.cybershield.protection.core.domain.Device;
import com.cybershield.protection.core.domain.type.DeviceType;
import com.cybershield.protection.core.domain.type.OsType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DeviceEntityTest {

    // scneario 1: tester la conversion bidirectionnelle entre Device (domaine) et DeviceEntity (entité JPA)
    @Test
    void shouldMapDomainToEntityAndBack() {
        // 1. GIVEN : Un Device complet
        UUID id = UUID.randomUUID();
        Device originalDomain = new Device(
                id,
                "00:11:22:33:44:55",
                "192.168.1.50",
                DeviceType.SERVER,      // Enum
                OsType.LINUX,           // Enum
                "Debian 11",
                "Srv-Prod",
                "HP",
                64,
                "22,80,443"
        );
        originalDomain.markAsProtected(); // Change le statut pour tester

        // 2. WHEN : Conversion vers Entity
        DeviceEntity entity = DeviceEntity.fromDomain(originalDomain);

        // Vérifications intermédiaires (Est-ce que l'Entity a bien reçu les Strings ?)
        assertEquals(id, entity.getId());
        assertEquals("SERVER", entity.getType());   // Enum -> String
        assertEquals("LINUX", entity.getOsType());  // Enum -> String
        assertEquals("PROTECTED", entity.getStatus());
        assertNotNull(entity.getEnrolledAt());

        // 3. WHEN : Retour vers Domaine
        Device resultDomain = entity.toDomain();

        // 4. THEN : Vérification finale (Tout doit correspondre)
        assertEquals(originalDomain.getId(), resultDomain.getId());
        assertEquals(originalDomain.getMacAddress(), resultDomain.getMacAddress());
        assertEquals(DeviceType.SERVER, resultDomain.getType()); // String -> Enum
        assertEquals(OsType.LINUX, resultDomain.getOsType());    // String -> Enum
        assertEquals(Device.DeviceStatus.PROTECTED, resultDomain.getStatus());
    }
}