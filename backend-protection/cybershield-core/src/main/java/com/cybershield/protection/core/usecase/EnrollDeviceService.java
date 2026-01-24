package com.cybershield.protection.core.usecase;

import com.cybershield.protection.core.domain.CompliancePolicy;
import com.cybershield.protection.core.domain.Device;
import com.cybershield.protection.core.domain.type.DeviceType;
import com.cybershield.protection.core.domain.type.OsType;
import com.cybershield.protection.core.port.in.EnrollDeviceUseCase;
import com.cybershield.protection.core.port.out.DeviceRepository;
import com.cybershield.protection.core.port.out.event.DeviceEventPublisher;

import java.util.UUID;
import java.util.Optional;

public class EnrollDeviceService implements EnrollDeviceUseCase {

    private final DeviceRepository deviceRepository;
    private final DeviceEventPublisher eventPublisher;
    private final CompliancePolicy compliancePolicy = new CompliancePolicy();

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

        if (existingDevice.isPresent()) {
            // --- CAS 1 : MISE À JOUR (UPDATE) ---
            device = existingDevice.get();

            // On met à jour les données qui ont pu changer (IP, Ports, etc.)
            // ⚠️ Il faut ajouter cette méthode dans Device.java (voir plus bas)
            device.updateNetworkInfo(ipAddress, hostname, vendor, ttl, openPorts);

            // On reset la recommandation pour forcer le recalcul avec les nouveaux ports
            device.setSecurityRecommendation(null);

        } else {
            // --- CAS 2 : CRÉATION (INSERT) ---
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
            // Notification seulement à la création (optionnel)
            eventPublisher.publishDeviceCreated(device);
        }

        // 3. Validation de conformité (Règles métier basiques)
        compliancePolicy.validate(device);

        // 4. Persistance (Sauvegarde ou Update)
        // Le calcul de sécurité (getSecurityRecommendation) se fera automatiquement
        // grâce à ton entité Device intelligente quand on la relira ou la convertira en JSON.
        return deviceRepository.save(device);
    }
}