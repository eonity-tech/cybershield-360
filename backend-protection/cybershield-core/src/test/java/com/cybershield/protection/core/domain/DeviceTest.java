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
        assertEquals("80,443", device.getOpenPorts()); // Ce test passe maintenant grâce à ta correction !
        assertNotNull(device.getEnrolledAt());
    }

    // --- SCÉNARIO 2. TESTS DE VALIDATION ---
    @Test
    void shouldThrowExceptionForInvalidMacAddress() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Device(UUID.randomUUID(), "BAD-MAC", "192.168.1.1",
                    DeviceType.COMPUTER, OsType.WINDOWS, "v1", "PC", "Vendor", 64, "80");
        });
    }

    @Test
    void shouldThrowExceptionForInvalidIpAddress() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Device(UUID.randomUUID(), "00:00:00:00:00:00", "999.999.999.999",
                    DeviceType.COMPUTER, OsType.WINDOWS, "v1", "PC", "Vendor", 64, "80");
        });
    }

    // --- SCÉNARIO 3. TEST DU SCORE DE RISQUE ---
    @Test
    void calculateRiskScore_HighRisk() {
        // Cas : Windows 7 (+50) + Port 21 (+30) + OS Inconnu (+20) = 100
        Device riskyDevice = new Device(
                UUID.randomUUID(), "00:11:22:33:44:55", "192.168.1.100",
                DeviceType.COMPUTER, OsType.UNKNOWN, "Windows 7",
                "Risky-PC", "Unknown", 128, "21,80"
        );

        assertEquals(100.0, riskyDevice.calculateRiskScore());
        assertTrue(riskyDevice.getSecurityRecommendation().contains("OS obsolète"));
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
        assertEquals("Appareil conforme aux standards de sécurité.", safeDevice.getSecurityRecommendation());
    }

    // --- SCÉNARIO 4. TEST DES CHANGEMENTS D'ÉTAT ---
    @Test
    void markAsProtected() {
        Device device = createDummyDevice();
        device.markAsProtected();
        assertEquals(Device.DeviceStatus.PROTECTED, device.getStatus());
    }

    @Test
    void markAsCompromised() {
        Device device = createDummyDevice();
        device.markAsCompromised();
        assertEquals(Device.DeviceStatus.COMPROMISED, device.getStatus());
    }

    private Device createDummyDevice() {
        return new Device(UUID.randomUUID(), "00:00:00:00:00:00", "127.0.0.1",
                DeviceType.OTHER, OsType.OTHER, "OS", "Host", "Vendor", 64, "");
    }
}