package com.cybershield.protection.core.usecase;

import com.cybershield.protection.core.domain.CompliancePolicy;
import com.cybershield.protection.core.domain.Device;
import com.cybershield.protection.core.domain.type.DeviceType;
import com.cybershield.protection.core.domain.type.OsType;
import com.cybershield.protection.core.port.in.EnrollDeviceUseCase;
import com.cybershield.protection.core.port.in.BlockDeviceUseCase; // Ajout de l'import
import com.cybershield.protection.core.port.out.DeviceRepository;
import com.cybershield.protection.core.port.out.event.DeviceEventPublisher;

import java.util.UUID;
import java.util.Optional;

// On implémente les deux contrats (In)
public class EnrollDeviceService implements EnrollDeviceUseCase, BlockDeviceUseCase {

    private final DeviceRepository deviceRepository;
    private final DeviceEventPublisher eventPublisher;
    private final CompliancePolicy compliancePolicy = new CompliancePolicy();

    // Constructeur unique et propre
    public EnrollDeviceService(DeviceRepository deviceRepository,
                               DeviceEventPublisher eventPublisher) {
        this.deviceRepository = deviceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Device enroll(String macAddress, String ipAddress, DeviceType type,
                         OsType osType, String osVersion, String hostname,
                         String vendor, Integer ttl, String openPorts) {

        Optional<Device> existingDevice = deviceRepository.findByMacAddress(macAddress);
        Device device;

        // 1. Vérification de l'existence
        if (existingDevice.isPresent()) {
            device = existingDevice.get();
            // Mise à jour des infos réseau
            device.updateNetworkInfo(ipAddress, hostname, vendor, ttl, openPorts);
            device.setSecurityRecommendation(null);
        } else {
            // 2. Création d'un nouveau Device
            device = new Device(
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
            eventPublisher.publishDeviceCreated(device);
        }

        compliancePolicy.validate(device);
        return deviceRepository.save(device);
    }

    // 3. Implémentation du blocage/déblocage des Devices
    @Override
    public void blockDevice(UUID deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device non trouvé"));

        device.setBlacklisted(true);
        deviceRepository.save(device);

        // on met en quarantaine
        eventPublisher.publishQuarantineAlert(
                device.getId(),
                "ACTION ADMIN : Blocage immédiat de l'appareil.",
                100.0 // Score maximum pour indiquer une menace élevée et action de l'IA
        );
    }

    // 4. Implémentation du déblocage des Devices
    @Override
    public void unblockDevice(UUID deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device non trouvé"));

        device.setBlacklisted(false);
        deviceRepository.save(device);

        // onlève la quarantaine
        eventPublisher.publishQuarantineAlert(
                device.getId(),
                "ACTION ADMIN : Restauration de l'accès.",
                0.0 // Score minimum pour indiquer que tout est normal
        );
    }
}