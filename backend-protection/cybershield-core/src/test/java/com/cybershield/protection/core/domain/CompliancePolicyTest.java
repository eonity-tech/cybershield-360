package com.cybershield.protection.core.domain;

import com.cybershield.protection.core.domain.type.DeviceType;
import com.cybershield.protection.core.domain.type.OsType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CompliancePolicyTest {

    private final CompliancePolicy policy = new CompliancePolicy();

    // --- SCÉNARIO 1 : Appareil conforme ---
    @Test
    void shouldAcceptCompliantDevice() {
        // GIVEN : Un PC standard sous Windows
        Device compliantDevice = createDevice(DeviceType.COMPUTER, OsType.WINDOWS, "Windows 10");

        // WHEN & THEN : Aucune exception ne doit être levée
        assertDoesNotThrow(() -> policy.validate(compliantDevice));
    }

    // --- SCÉNARIO 2 : Violation Règle 1 (OS Inconnu) ---
    @Test
    void shouldRejectDeviceWithUnknownOs() {
        // GIVEN : Un appareil dont l'OS n'est pas reconnu
        Device riskyDevice = createDevice(DeviceType.IOT, OsType.UNKNOWN, "Some Firmware");

        // WHEN
        CompliancePolicy.ComplianceException exception = assertThrows(
                CompliancePolicy.ComplianceException.class,
                () -> policy.validate(riskyDevice)
        );

        // THEN : On vérifie le message d'erreur
        assertEquals("Sécurité : Les appareils avec un OS 'UNKNOWN' sont interdits.", exception.getMessage());
    }

    // ---SCÉNARIO 3 : Violation Règle 2 (Serveur sans version) ---
    @Test
    void shouldRejectServerWithMissingOsVersion() {
        // GIVEN : Un Serveur dont la version est "N/A"
        Device badServer = createDevice(DeviceType.SERVER, OsType.LINUX, "N/A");

        // WHEN
        CompliancePolicy.ComplianceException exception = assertThrows(
                CompliancePolicy.ComplianceException.class,
                () -> policy.validate(badServer)
        );

        // THEN
        assertTrue(exception.getMessage().contains("Un serveur doit spécifier sa version d'OS"));
    }

    // --- SCÉNARIO 4 : Cas Limite (Serveur AVEC version) ---
    @Test
    void shouldAcceptServerWithVersion() {
        // GIVEN : Un Serveur correctement configuré
        Device goodServer = createDevice(DeviceType.SERVER, OsType.LINUX, "Ubuntu 22.04 LTS");

        // WHEN & THEN
        assertDoesNotThrow(() -> policy.validate(goodServer));
    }

    // Utilitaires

    // Créer rapidement un Device sans répéter tout le code
    private Device createDevice(DeviceType type, OsType osType, String osVersion) {
        return new Device(
                UUID.randomUUID(),
                "00:0a:95:9d:68:16", // MAC valide
                "192.168.1.50", // IP valide
                type,
                osType,
                osVersion,
                "Test-Host",
                "Test-Vendor",
                64,
                "80"
        );
    }
}