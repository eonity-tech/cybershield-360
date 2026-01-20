package com.cybershield.protection.core.usecase;

import com.cybershield.protection.core.domain.Software;
import com.cybershield.protection.core.port.in.RegisterSoftwareUseCase;
import com.cybershield.protection.core.port.out.DeviceRepository;
import com.cybershield.protection.core.port.out.SoftwareRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
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
    public Software register(UUID deviceId, String name, String version, String publisher, boolean isRunning) {

        // 1. GARDE DE SÉCURITÉ : Vérification de la présence de l'ID
        if (deviceId == null) {
            throw new IllegalArgumentException("L'identifiant de l'appareil est obligatoire pour enregistrer un logiciel.");
        }

        // 2. VÉRIFICATION D'EXISTENCE (Optionnel mais recommandé)
        // On s'assure que le device existe vraiment en BDD avant de lui lier un software
        if (!deviceRepository.existsById(deviceId)) {
            throw new NoSuchElementException("Impossible d'enregistrer le logiciel : l'appareil avec l'ID " + deviceId + " n'existe pas.");
        }

        Software newSoftware = new Software(
                UUID.randomUUID(),
                deviceId,
                name,
                version,
                publisher,
                isRunning
        );

        return softwareRepository.save(newSoftware);
    }

    @Override
    public List<Software> findByDeviceId(UUID deviceId) {
        if (deviceId == null) return List.of();
        return softwareRepository.findByDeviceId(deviceId);
    }

    @Override
    public List<Software> findCriticalSoftware() {
        // Logique pour filtrer les logiciels à risque (ex: versions obsolètes)
        return softwareRepository.findAll().stream()
                .filter(s -> s.getVersion().contains("beta") || s.getName().equalsIgnoreCase("wireshark"))
                .toList();
    }
}