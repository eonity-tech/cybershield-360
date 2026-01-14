package com.cybershield.protection.core.usecase;

import com.cybershield.protection.core.domain.Software;
import com.cybershield.protection.core.port.in.RegisterSoftwareUseCase;
import com.cybershield.protection.core.port.out.DeviceRepository;
import com.cybershield.protection.core.port.out.SoftwareRepository;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.List;

@Service
public class SoftwareService implements RegisterSoftwareUseCase {

    private final DeviceRepository deviceRepository;
    private final SoftwareRepository softwareRepository; // <-- Port de sortie à injecter

    public SoftwareService(DeviceRepository deviceRepository, SoftwareRepository softwareRepository) {
        this.deviceRepository = deviceRepository;
        this.softwareRepository = softwareRepository;
    }

    @Override
    public Software register(UUID deviceId, String name, String version, String publisher, boolean isRunning) {
        // Optionnel : vérifier ici si le deviceId existe via deviceRepository

        Software newSoftware = new Software(
                UUID.randomUUID(),
                deviceId,
                name,
                version,
                publisher,
                isRunning
        );

        return softwareRepository.save(newSoftware); // <-- On enregistre vraiment !
    }

    @Override
    public List<Software> findByDeviceId(UUID deviceId) {
        return softwareRepository.findByDeviceId(deviceId); // <-- Utilise le repo
    }

    @Override
    public List<Software> findCriticalSoftware() {
        // Logique pour filtrer les logiciels avec un score élevé
        return List.of();
    }
}