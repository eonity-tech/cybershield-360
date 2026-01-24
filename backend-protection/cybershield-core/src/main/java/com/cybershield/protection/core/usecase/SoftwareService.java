package com.cybershield.protection.core.usecase;

import com.cybershield.protection.core.domain.Software;
import com.cybershield.protection.core.port.in.RegisterSoftwareUseCase;
import com.cybershield.protection.core.port.out.DeviceRepository;
import com.cybershield.protection.core.port.out.SoftwareRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Service
public class SoftwareService implements RegisterSoftwareUseCase {

    private final DeviceRepository deviceRepository;
    private final SoftwareRepository softwareRepository;

    public SoftwareService(DeviceRepository deviceRepository, SoftwareRepository softwareRepository) {
        this.deviceRepository = deviceRepository;
        this.softwareRepository = softwareRepository;
    }

    @Override
    public Software register(UUID deviceId, String name, String version, String type, String publisher, boolean isRunning) {

        // 1. Garde-fou simple
        if (deviceId == null) {
            throw new IllegalArgumentException("L'identifiant de l'appareil est obligatoire.");
        }

        // 2. Vérification de l'existence de l'appareil
        if (!deviceRepository.existsById(deviceId)) {
            throw new NoSuchElementException("Impossible d'enregistrer le logiciel : l'appareil " + deviceId + " n'existe pas.");
        }

        // 3. Logique UPSERT (Anti-Doublon)
        Optional<Software> existingSoftware = softwareRepository.findByDeviceIdAndName(deviceId, name);

        if (existingSoftware.isPresent()) {
            // Mise à jour
            Software softwareToUpdate = existingSoftware.get();
            softwareToUpdate.updateInfo(version, type);
            return softwareRepository.save(softwareToUpdate);
        } else {
            // Création
            Software newSoftware = new Software(
                    UUID.randomUUID(),
                    deviceId,
                    name,
                    version,
                    type,
                    publisher,
                    isRunning
            );
            return softwareRepository.save(newSoftware);
        }
    }

    @Override
    public List<Software> findByDeviceId(UUID deviceId) {
        if (deviceId == null) return List.of();
        return softwareRepository.findByDeviceId(deviceId);
    }

    @Override
    public List<Software> findCriticalSoftware() {
        return softwareRepository.findAll().stream()
                .filter(s -> {
                    // Exemple de règle métier : Score > 80 ou nom suspect
                    return s.getCriticalScore() != null && s.getCriticalScore() > 80;
                })
                .toList();
    }
}