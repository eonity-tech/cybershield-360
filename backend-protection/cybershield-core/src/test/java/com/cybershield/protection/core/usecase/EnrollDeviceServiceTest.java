package com.cybershield.protection.core.usecase;

import com.cybershield.protection.core.domain.CompliancePolicy;
import com.cybershield.protection.core.domain.Device;
import com.cybershield.protection.core.domain.type.DeviceType;
import com.cybershield.protection.core.domain.type.OsType;
import com.cybershield.protection.core.port.out.DeviceRepository;
import com.cybershield.protection.core.port.out.event.DeviceEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollDeviceServiceTest {

    // 1. Les Mocks (Gérés par MockitoExtension)
    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private DeviceEventPublisher eventPublisher;

    // 2. Le service d'analyse de sécurité réel
    private final SecurityAnalyzerService securityAnalyzerService = new SecurityAnalyzerService();

    // 3. Le Service à tester
    private EnrollDeviceService enrollDeviceService;

    @BeforeEach
    void setUp() {
        enrollDeviceService = new EnrollDeviceService(deviceRepository, eventPublisher, securityAnalyzerService);
    }

    // --- SCÉNARIO 1 : Succès ---
    @Test
    void shouldEnrollDeviceSuccessfully() {
        // GIVEN
        String mac = "00:11:22:33:44:55";
        String ip = "192.168.1.10";

        when(deviceRepository.findByMacAddress(mac)).thenReturn(Optional.empty());
        when(deviceRepository.save(any(Device.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        Device result = enrollDeviceService.enroll(
                mac, ip, DeviceType.COMPUTER, OsType.LINUX, "Ubuntu 22.04",
                "My-Host", "Dell", 64, "80,443"
        );

        // THEN
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(mac, result.getMacAddress());

        // On vérifie le rapport de sécurité généré
        assertNotNull(result.getSecurityRecommendation());
        assertTrue(result.getSecurityRecommendation().contains("Appareil sain"));

        verify(deviceRepository).save(any(Device.class));
        verify(eventPublisher).publishDeviceCreated(result);
    }

    // --- SCÉNARIO 2 : Erreur - Doublon ---
    @Test
    void shouldThrowExceptionWhenDeviceAlreadyExists() {
        // GIVEN
        String mac = "AA:BB:CC:DD:EE:FF";
        Device existingDevice = new Device(UUID.randomUUID(), mac, "10.0.0.1", DeviceType.OTHER, OsType.OTHER, "v1", "h", "v", 64, "");

        when(deviceRepository.findByMacAddress(mac)).thenReturn(Optional.of(existingDevice));

        // WHEN & THEN
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            enrollDeviceService.enroll(
                    mac, "192.168.1.20", DeviceType.IOT, OsType.OTHER, "Firmware",
                    "Cam-01", "Sony", 64, "80"
            );
        });

        assertEquals("L'appareil avec l'adresse MAC " + mac + " est déjà enregistré.", exception.getMessage());
        verify(deviceRepository, never()).save(any());
        verify(eventPublisher, never()).publishDeviceCreated(any());
    }

    // --- SCÉNARIO 3 : Erreur - Non Conforme ---
    @Test
    void shouldRejectNonCompliantDevice() {
        // GIVEN
        String mac = "11:22:33:44:55:66";
        when(deviceRepository.findByMacAddress(mac)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(CompliancePolicy.ComplianceException.class, () -> {
            enrollDeviceService.enroll(
                    mac, "10.0.0.50", DeviceType.IOT,
                    OsType.UNKNOWN, // Interdit
                    "Chinese Firmware v1",
                    "Unknown-Cam", "NoName", 64, "23"
            );
        });

        verify(deviceRepository, never()).save(any());
    }
}