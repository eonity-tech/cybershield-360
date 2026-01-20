package com.cybershield.protection.core.usecase;

import com.cybershield.protection.core.domain.CompliancePolicy;
import com.cybershield.protection.core.domain.Device;
import com.cybershield.protection.core.domain.type.DeviceType;
import com.cybershield.protection.core.domain.type.OsType;
import com.cybershield.protection.core.port.out.DeviceRepository;
import com.cybershield.protection.core.port.out.event.DeviceEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Active Mockito pour créer les faux objets
class EnrollDeviceServiceTest {

    // 1. On crée les Mocks (les doublures)
    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private DeviceEventPublisher eventPublisher;

    // 2. On injecte les mocks dans le service à tester
    @InjectMocks
    private EnrollDeviceService enrollDeviceService;

    // --- SCÉNARIO 1 : Succès (Happy Path) ---
    @Test
    void shouldEnrollDeviceSuccessfully() {
        // GIVEN : Des données valides
        String mac = "00:11:22:33:44:55";
        String ip = "192.168.1.10";

        // Simulation : Le repository dit "Je ne connais pas cette adresse MAC" (Optional.empty)
        when(deviceRepository.findByMacAddress(mac)).thenReturn(Optional.empty());

        // Simulation : Quand on sauvegarde, on renvoie l'objet (simule le comportement de JPA)
        when(deviceRepository.save(any(Device.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN : On appelle le service
        Device result = enrollDeviceService.enroll(
                mac, ip, DeviceType.COMPUTER, OsType.LINUX, "Ubuntu 22.04",
                "My-Host", "Dell", 64, "80,443"
        );

        // THEN : Vérifications
        assertNotNull(result);
        assertNotNull(result.getId(), "L'ID doit être généré");
        assertEquals(mac, result.getMacAddress());

        // Vérification cruciale : Est-ce que les dépendances ont bien été appelées ?
        verify(deviceRepository).save(any(Device.class));       // Sauvegarde appelée ?
        verify(eventPublisher).publishDeviceCreated(result);    // Notification envoyée ?
    }

    // --- SCÉNARIO 2 : Erreur - Doublon (L'appareil existe déjà) ---
    @Test
    void shouldThrowExceptionWhenDeviceAlreadyExists() {
        // GIVEN
        String mac = "AA:BB:CC:DD:EE:FF";

        // Simulation : Le repository dit "Oui, je connais déjà cet appareil"
        Device existingDevice = new Device(UUID.randomUUID(), mac, "10.0.0.1", DeviceType.OTHER, OsType.OTHER, "v1", "h", "v", 64, "");
        when(deviceRepository.findByMacAddress(mac)).thenReturn(Optional.of(existingDevice));

        // WHEN & THEN : On s'attend à une exception IllegalStateException
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            enrollDeviceService.enroll(
                    mac, "192.168.1.20", DeviceType.IOT, OsType.OTHER, "Firmware",
                    "Cam-01", "Sony", 64, "80"
            );
        });

        assertEquals("L'appareil avec l'adresse MAC " + mac + " est déjà enregistré.", exception.getMessage());

        // Vérification : On ne doit SURTOUT PAS sauvegarder ni notifier
        verify(deviceRepository, never()).save(any());
        verify(eventPublisher, never()).publishDeviceCreated(any());
    }

    // --- SCÉNARIO 3 : Erreur - Non Conforme (Règles de sécurité) ---
    @Test
    void shouldRejectNonCompliantDevice() {
        // GIVEN : Un appareil avec un OS inconnu (Interdit par CompliancePolicy)
        String mac = "11:22:33:44:55:66";

        // Le repo ne le connait pas (donc pas de doublon)
        when(deviceRepository.findByMacAddress(mac)).thenReturn(Optional.empty());

        // WHEN & THEN : On s'attend à une exception de Compliance
        assertThrows(CompliancePolicy.ComplianceException.class, () -> {
            enrollDeviceService.enroll(
                    mac, "10.0.0.50", DeviceType.IOT,
                    OsType.UNKNOWN, // <--- C'est ça qui doit faire planter
                    "Chinese Firmware v1",
                    "Unknown-Cam", "NoName", 64, "23"
            );
        });

        // Vérification : Comme il est dangereux, on ne le sauvegarde pas
        verify(deviceRepository, never()).save(any());
        verify(eventPublisher, never()).publishDeviceCreated(any());
    }
}