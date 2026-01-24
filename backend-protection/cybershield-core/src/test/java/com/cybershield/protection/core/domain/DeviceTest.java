package com.cybershield.protection.core.domain;

import com.cybershield.protection.core.domain.type.DeviceType;
import com.cybershield.protection.core.domain.type.OsType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DeviceTest {

    // --- SCÉNARIO 1. TEST DE CRÉATION & GETTERS ---
    @Test
    void shouldCreateDeviceWithValidData() {
        UUID id = UUID.randomUUID();
        String mac = "00:0a:95:9d:68:16";
        String ip = "192.168.1.50";

        Device device = new Device(
                id, mac, ip,
                DeviceType.COMPUTER, OsType.LINUX, "Ubuntu 22.04",
                "My-PC", "Dell", 64, "80,443"
        );

        assertEquals(id, device.getId());
        assertEquals(mac, device.getMacAddress());
        assertEquals("80,443", device.getOpenPorts());
        assertNotNull(device.getEnrolledAt());
    }

    // --- SCÉNARIO 2. TESTS DE VALIDATION ---
    @Test
    void shouldThrowExceptionForInvalidMacAddress() {
        assertThrows(IllegalArgumentException.class, () -> new Device(UUID.randomUUID(), "BAD-MAC", "192.168.1.1",
                DeviceType.COMPUTER, OsType.WINDOWS, "v1", "PC", "Vendor", 64, "80"));
    }

    @Test
    void shouldThrowExceptionForInvalidIpAddress() {
        assertThrows(IllegalArgumentException.class, () -> new Device(UUID.randomUUID(), "00:00:00:00:00:00", "999.999.999.999",
                DeviceType.COMPUTER, OsType.WINDOWS, "v1", "PC", "Vendor", 64, "80"));
    }

    // --- SCÉNARIO 3. TEST DU SCORE DE RISQUE (Logique interne) ---
    @Test
    void calculateRiskScore_HighRisk() {
        // Cas : Windows 7 (+60) + Port 21 (+30) + OS Inconnu (+10) = 100
        Device riskyDevice = new Device(
                UUID.randomUUID(), "00:11:22:33:44:55", "192.168.1.100",
                DeviceType.COMPUTER, OsType.UNKNOWN, "Windows 7",
                "Risky-PC", "Unknown", 128, "21,80"
        );

        assertEquals(100.0, riskyDevice.calculateRiskScore());

        // ✅ CORRECTION : On vérifie les nouveaux messages générés par Device.java
        // "Windows 7" déclenche "OS Obsolète"
        assertTrue(riskyDevice.getSecurityRecommendation().contains("Obsolète"));
        // Port 21 déclenche "FTP"
        assertTrue(riskyDevice.getSecurityRecommendation().contains("FTP"));
    }

    @Test
    void calculateRiskScore_LowRisk() {
        Device safeDevice = new Device(
                UUID.randomUUID(), "AA:BB:CC:DD:EE:FF", "10.0.0.1",
                DeviceType.SERVER, OsType.LINUX, "Ubuntu",
                "Safe-Srv", "Canonical", 64, "443"
        );

        assertEquals(0.0, safeDevice.calculateRiskScore());

        // ✅ CORRECTION : Mise à jour du message attendu (nouveau format)
        assertEquals("Appareil sain. Aucune vulnérabilité critique détectée sur les ports analysés.",
                safeDevice.getSecurityRecommendation());
    }

    // --- SCÉNARIO 4. TEST DU RAPPORT IA (Le Setter) ---
    @Test
    void shouldPrioritizeManualSecurityRecommendation() {
        Device device = createDummyDevice();

        // 1. Au début, c'est le message par défaut (Sain)
        // ✅ CORRECTION : On cherche "sain" au lieu de "conforme"
        assertTrue(device.getSecurityRecommendation().contains("sain"));

        // 2. On simule l'expert IA qui injecte son rapport
        String aiReport = "ALERTES SÉCURITÉ : Port RDP détecté par l'IA";
        device.setSecurityRecommendation(aiReport);

        // 3. Le getter doit renvoyer le rapport IA
        assertEquals(aiReport, device.getSecurityRecommendation());
    }

    // --- SCÉNARIO 5. TEST DE SANITIZATION ---
    @Test
    void shouldSanitizeInputs() {
        Device device = new Device(
                UUID.randomUUID(), "00:11:22:33:44:55", "192.168.1.1",
                DeviceType.COMPUTER, OsType.LINUX,
                "Ubuntu <script>", // Injection HTML
                "Host", "Vendor", 64, "80; DROP TABLE" // Injection SQL
        );

        assertEquals("Ubuntu script", device.getOsVersion());
        // La virgule est autorisée maintenant, mais le point-virgule doit partir
        assertFalse(device.getOpenPorts().contains(";"));
    }

    // --- SCÉNARIO 6. TEST DU BLACKLISTAGE (Kill Switch) ---
    @Test
    void shouldToggleBlacklistStatus() {
        // GIVEN: Un appareil sain par défaut
        Device device = createDummyDevice();
        assertFalse(device.isBlacklisted(), "Un nouvel appareil ne doit pas être blacklisté par défaut");

        // WHEN: L'administrateur active le blocage
        device.setBlacklisted(true);

        // THEN: L'état doit être mis à jour
        assertTrue(device.isBlacklisted());

        // WHEN: L'administrateur lève la sanction
        device.setBlacklisted(false);

        // THEN: L'appareil doit redevenir autorisé
        assertFalse(device.isBlacklisted());
    }

    // --- Helpers ---
    private Device createDummyDevice() {
        return new Device(UUID.randomUUID(), "00:00:00:00:00:00", "127.0.0.1",
                DeviceType.OTHER, OsType.OTHER, "OS", "Host", "Vendor", 64, "");
    }
}