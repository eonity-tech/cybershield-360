package com.cybershield.protection.core.usecase;

import com.cybershield.protection.core.domain.Software;
import com.cybershield.protection.core.port.out.DeviceRepository;
import com.cybershield.protection.core.port.out.SoftwareRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Active Mockito
class SoftwareServiceTest {

    @Mock
    private DeviceRepository deviceRepository; // Simule le port Device

    @Mock
    private SoftwareRepository softwareRepository; // Simule le port Software

    @InjectMocks
    private SoftwareService softwareService; // Le service à tester

    // --- TEST 1 : ENREGISTREMENT ---
    @Test
    @DisplayName("Register : Doit sauvegarder le logiciel si l'appareil existe")
    void register() {
        // GIVEN (Préparation)
        UUID deviceId = UUID.randomUUID();
        when(deviceRepository.existsById(deviceId)).thenReturn(true); // L'appareil existe
        when(softwareRepository.save(any(Software.class))).thenAnswer(i -> i.getArgument(0)); // Mock save

        // WHEN (Action)
        Software result = softwareService.register(deviceId, "Nmap", "7.92", "Insecure.org", true);

        // THEN (Vérification)
        assertNotNull(result);
        assertEquals("Nmap", result.getName());
        assertEquals(deviceId, result.getDeviceId());
        verify(softwareRepository).save(any(Software.class)); // Vérifie que save() a été appelé
    }

    @Test
    @DisplayName("Register : Doit échouer si l'appareil n'existe pas")
    void register_ShouldThrowException_WhenDeviceNotFound() {
        // GIVEN
        UUID unknownId = UUID.randomUUID();
        when(deviceRepository.existsById(unknownId)).thenReturn(false); // L'appareil n'existe pas

        // WHEN & THEN
        assertThrows(NoSuchElementException.class, () ->
                softwareService.register(unknownId, "Virus", "1.0", "Hacker", true)
        );

        verify(softwareRepository, never()).save(any()); // Vérifie qu'on n'a RIEN sauvegardé
    }

    // --- TEST 2 : RECHERCHE PAR DEVICE ---
    @Test
    @DisplayName("FindByDeviceId : Doit retourner la liste des logiciels")
    void findByDeviceId() {
        // GIVEN
        UUID deviceId = UUID.randomUUID();
        List<Software> mockSoftwares = List.of(
                new Software(UUID.randomUUID(), deviceId, "Java", "17", "Oracle", true),
                new Software(UUID.randomUUID(), deviceId, "Docker", "20", "Docker Inc", true)
        );
        when(softwareRepository.findByDeviceId(deviceId)).thenReturn(mockSoftwares);

        // WHEN
        List<Software> result = softwareService.findByDeviceId(deviceId);

        // THEN
        assertEquals(2, result.size());
        assertEquals("Java", result.get(0).getName());
    }

    // --- TEST 3 : DÉTECTION LOGICIELS CRITIQUES ---
    @Test
    @DisplayName("FindCriticalSoftware : Doit filtrer Wireshark et versions Beta")
    void findCriticalSoftware() {
        // GIVEN
        List<Software> allSoftwares = List.of(
                new Software(UUID.randomUUID(), UUID.randomUUID(), "Word", "2019", "Microsoft", false),
                new Software(UUID.randomUUID(), UUID.randomUUID(), "Wireshark", "3.0", "Wireshark", true), // Critique !
                new Software(UUID.randomUUID(), UUID.randomUUID(), "Chrome", "99-beta", "Google", true)   // Critique !
        );
        when(softwareRepository.findAll()).thenReturn(allSoftwares);

        // WHEN
        List<Software> criticals = softwareService.findCriticalSoftware();

        // THEN
        assertEquals(2, criticals.size(), "Doit trouver 2 logiciels critiques");
        assertTrue(criticals.stream().anyMatch(s -> s.getName().equals("Wireshark")));
        assertTrue(criticals.stream().anyMatch(s -> s.getVersion().contains("beta")));
    }
}