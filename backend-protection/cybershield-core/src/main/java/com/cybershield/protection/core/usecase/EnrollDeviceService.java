package com.cybershield.protection.core.usecase;

import com.cybershield.protection.core.domain.CompliancePolicy;
import com.cybershield.protection.core.domain.Device;
import com.cybershield.protection.core.domain.type.DeviceType;
import com.cybershield.protection.core.domain.type.OsType;
import com.cybershield.protection.core.port.in.EnrollDeviceUseCase;
import com.cybershield.protection.core.port.out.DeviceRepository;
import com.cybershield.protection.core.port.out.event.DeviceEventPublisher;

import java.util.UUID;

public class EnrollDeviceService implements EnrollDeviceUseCase {

    private final DeviceRepository deviceRepository;
    private final DeviceEventPublisher eventPublisher;
    private final CompliancePolicy compliancePolicy = new CompliancePolicy();

    public EnrollDeviceService(DeviceRepository deviceRepository, DeviceEventPublisher eventPublisher) {
        this.deviceRepository = deviceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Device enroll(String macAddress, String ipAddress, DeviceType type,
                         OsType osType, String osVersion, String hostname,
                         String vendor, Integer ttl, String openPorts) {

        // 1. Anti-doublon
        if (deviceRepository.findByMacAddress(macAddress).isPresent()) {
            throw new IllegalStateException("L'appareil avec l'adresse MAC " + macAddress + " est déjà enregistré.");
        }

        // 2. Création de l'entité avec les 9 paramètres (+ UUID généré)
        Device newDevice = new Device(
                UUID.randomUUID(),
                macAddress,
                ipAddress,
                type,
                osType,
                osVersion,
                hostname,
                vendor,
                ttl,
                openPorts
        );

        // 3. Sécurité : Validation de conformité
        compliancePolicy.validate(newDevice);

        // 4. Persistance
        Device savedDevice = deviceRepository.save(newDevice);

        // 5. Notification (Redis)
        eventPublisher.publishDeviceCreated(savedDevice);

        return savedDevice;
    }
}