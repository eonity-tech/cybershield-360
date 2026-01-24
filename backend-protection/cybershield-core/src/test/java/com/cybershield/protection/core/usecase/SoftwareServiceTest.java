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
import java.util.Optional; // ✅ Import indispensable pour le mock
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SoftwareServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private SoftwareRepository softwareRepository;

    @InjectMocks
    private SoftwareService softwareService;

    // --- TEST 1 : ENREGISTREMENT, nouveau logiciel ---
    @Test
    @DisplayName("Register : Doit créer un nouveau logiciel si l'appareil existe et qu'il n'y a pas de doublon")
    void register_NewSoftware() {
        // GIVEN
        UUID deviceId = UUID.randomUUID();
        String softwareName = "Nmap";

        // 1. Le device existe
        when(deviceRepository.existsById(deviceId)).thenReturn(true);

        // 2. Le logiciel N'EXISTE PAS encore (en base) pour cet appareil
        when(softwareRepository.findByDeviceIdAndName(deviceId, softwareName)).thenReturn(Optional.empty());

        // 3. Mock du save
        when(softwareRepository.save(any(Software.class))).thenAnswer(i -> i.getArgument(0));

        // WHEN
        // Ajout du paramètre "Network Tool" (type)
        Software result = softwareService.register(deviceId, softwareName, "7.92", "Network Tool", "Insecure.org", true);

        // THEN
        assertNotNull(result);
        assertEquals("Nmap", result.getName());
        assertEquals("Network Tool", result.getType()); // Vérif du type
        verify(softwareRepository).save(any(Software.class));
    }

    // --- TEST 1 BIS : MISE A JOUR D'UN LOGICIEL EXISTANT ---
    @Test
    @DisplayName("Register : Doit mettre à jour le logiciel s'il existe déjà (Upsert)")
    void register_UpdateExisting() {
        // GIVEN
        UUID deviceId = UUID.randomUUID();
        String softwareName = "VLC";

        // Un logiciel existe déjà en base avec une vieille version
        Software existingSoftware = new Software(UUID.randomUUID(), deviceId, softwareName, "1.0", "Media Player", "VideoLAN", true);

        when(deviceRepository.existsById(deviceId)).thenReturn(true);

        // Le logiciel EXISTE DÉJÀ -> Le service doit faire une mise à jour
        when(softwareRepository.findByDeviceIdAndName(deviceId, softwareName)).thenReturn(Optional.of(existingSoftware));

        when(softwareRepository.save(any(Software.class))).thenAnswer(i -> i.getArgument(0));

        // WHEN : On enregistre une NOUVELLE version
        Software result = softwareService.register(deviceId, softwareName, "2.0", "Media Player", "VideoLAN", true);

        // THEN
        assertEquals("2.0", result.getVersion()); // La version a changé
        assertEquals(existingSoftware.getId(), result.getId()); // C'est toujours le même ID (pas de doublon)
        verify(softwareRepository).save(existingSoftware);
    }

    @Test
    @DisplayName("Register : Doit échouer si l'appareil n'existe pas")
    void register_ShouldThrowException_WhenDeviceNotFound() {
        // GIVEN
        UUID unknownId = UUID.randomUUID();
        when(deviceRepository.existsById(unknownId)).thenReturn(false);

        // WHEN & THEN
        assertThrows(NoSuchElementException.class, () ->
                // ✅ Ajout du paramètre type "Malware"
                softwareService.register(unknownId, "Virus", "1.0", "Malware", "Hacker", true)
        );

        verify(softwareRepository, never()).save(any());
    }

    // --- TEST 2 : RECHERCHE PAR DEVICE ---
    @Test
    @DisplayName("FindByDeviceId : Doit retourner la liste des logiciels")
    void findByDeviceId() {
        // GIVEN
        UUID deviceId = UUID.randomUUID();
        List<Software> mockSoftwares = List.of(
                // ✅ Ajout du paramètre type dans le constructeur
                new Software(UUID.randomUUID(), deviceId, "Java", "17", "Dev", "Oracle", true),
                new Software(UUID.randomUUID(), deviceId, "Docker", "20", "Dev", "Docker Inc", true)
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
                new Software(UUID.randomUUID(), UUID.randomUUID(), "Word", "2019", "Office", "Microsoft", false),
                new Software(UUID.randomUUID(), UUID.randomUUID(), "Wireshark", "3.0", "Network", "Wireshark", true),
                new Software(UUID.randomUUID(), UUID.randomUUID(), "Chrome", "99-beta", "Browser", "Google", true)
        );
        when(softwareRepository.findAll()).thenReturn(allSoftwares);

        // WHEN
        List<Software> criticals = softwareService.findCriticalSoftware();

        // THEN
        assertEquals(2, criticals.size());
        assertTrue(criticals.stream().anyMatch(s -> s.getName().equals("Wireshark")));
    }
}