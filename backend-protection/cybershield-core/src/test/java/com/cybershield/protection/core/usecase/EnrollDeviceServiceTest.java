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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollDeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private DeviceEventPublisher eventPublisher;

    private EnrollDeviceService enrollDeviceService;

    @BeforeEach
    void setUp() {
        // Initialisation du service avec les mocks
        enrollDeviceService = new EnrollDeviceService(deviceRepository, eventPublisher);
    }

    // --- SCÉNARIO 1 : Création (Nouveau Device) ---
    @Test
    void shouldEnrollNewDeviceSuccessfully() {
        // GIVEN
        String mac = "00:11:22:33:44:55";
        String ip = "192.168.1.10";

        // Simule qu'aucun device n'existe avec cette MAC
        when(deviceRepository.findByMacAddress(mac)).thenReturn(Optional.empty());

        // Simule la sauvegarde (retourne l'objet qu'on lui donne)
        when(deviceRepository.save(any(Device.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        Device result = enrollDeviceService.enroll(
                mac, ip, DeviceType.COMPUTER, OsType.LINUX, "Ubuntu 22.04",
                "My-Host", "Dell", 64, "443" // Port safe
        );

        // THEN
        assertNotNull(result);
        assertEquals(mac, result.getMacAddress());

        // Vérifie que l'analyse de sécurité interne a fonctionné (Port 443 = Score 0 = Sain)
        assertTrue(result.getSecurityRecommendation().contains("Appareil sain"));

        verify(deviceRepository).save(any(Device.class));
        // En création, on DOIT publier l'événement
        verify(eventPublisher).publishDeviceCreated(result);
    }

    // --- SCÉNARIO 2 : Mise à jour (Device existant) ---
    @Test
    void shouldUpdateDeviceWhenAlreadyExists() {
        // GIVEN
        String mac = "AA:BB:CC:DD:EE:FF";
        UUID existingId = UUID.randomUUID();

        // Un appareil existant avec une vieille IP et des ports sains
        Device existingDevice = new Device(existingId, mac, "10.0.0.1", DeviceType.SERVER, OsType.LINUX, "v1", "OldHost", "OldVendor", 64, "80");
        existingDevice.setSecurityRecommendation("Old Report"); // On simule un vieux rapport

        when(deviceRepository.findByMacAddress(mac)).thenReturn(Optional.of(existingDevice));
        when(deviceRepository.save(any(Device.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN : On enrôle le MÊME appareil, mais avec des nouvelles infos (Nouvlle IP + Port RDP Dangereux)
        Device result = enrollDeviceService.enroll(
                mac, "192.168.1.99", DeviceType.SERVER, OsType.LINUX, "v1",
                "NewHost", "NewVendor", 64, "3389" // 🚨 Port RDP ajouté !
        );

        // THEN
        assertEquals(existingId, result.getId()); // L'ID ne doit pas changer
        assertEquals("192.168.1.99", result.getIpAddress()); // L'IP doit être mise à jour
        assertEquals("3389", result.getOpenPorts()); // Les ports doivent être mis à jour

        // VÉRIFICATION CLEF : Le rapport de sécurité a dû changer !
        assertFalse(result.getSecurityRecommendation().contains("Old Report"));
        assertTrue(result.getSecurityRecommendation().contains("URGENT")); // Car RDP = Urgent

        verify(deviceRepository).save(existingDevice);

        // En mise à jour, on NE DOIT PAS publier l'événement
        verify(eventPublisher, never()).publishDeviceCreated(any());
    }

    // --- SCÉNARIO 3 : Erreur - Non Conforme ---
    @Test
    void shouldRejectNonCompliantDevice() {
        // GIVEN
        String mac = "11:22:33:44:55:66";
        // Peu importe si le device existe ou pas, la conformité est vérifiée avant/pendant
        when(deviceRepository.findByMacAddress(mac)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(CompliancePolicy.ComplianceException.class, () -> {
            enrollDeviceService.enroll(
                    mac, "10.0.0.50", DeviceType.IOT,
                    OsType.UNKNOWN, // ❌ OS Inconnu = Interdit par la Policy
                    "Chinese Firmware v1",
                    "Unknown-Cam", "NoName", 64, "23"
            );
        });

        verify(deviceRepository, never()).save(any());
    }

    // --- SCÉNARIO 4 : Blocage Administrateur (Phase 6) ---
    @Test
    void shouldBlockDeviceAndPublishAlert() {
        // GIVEN
        UUID deviceId = UUID.randomUUID();
        Device targetDevice = new Device(
                deviceId, "00:AA:BB:CC:DD:EE", "192.168.1.15",
                DeviceType.COMPUTER, OsType.WINDOWS, "11",
                "PC-TARGET", "Dell", 128, "443"
        );

        // Simulation de la présence du device en base
        when(deviceRepository.findById(deviceId)).thenReturn(Optional.of(targetDevice));
        when(deviceRepository.save(any(Device.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        enrollDeviceService.blockDevice(deviceId);

        // THEN
        // 1. Vérifier que l'état interne du device a changé
        assertTrue(targetDevice.isBlacklisted(), "Le device devrait être marqué comme blacklisté");

        // 2. Vérifier que la persistance a été appelée
        verify(deviceRepository).save(targetDevice);

        // 3. Vérifier que l'ordre de Quarantaine a été envoyé à Redis (Phase 6)
        ArgumentCaptor<String> reasonCaptor = ArgumentCaptor.forClass(String.class);
        verify(eventPublisher).publishQuarantineAlert(
                eq(deviceId),
                reasonCaptor.capture(),
                eq(100.0)
        );

        // Le motif doit mentionner l'action admin
        assertTrue(reasonCaptor.getValue().contains("ACTION ADMIN"));
    }
}