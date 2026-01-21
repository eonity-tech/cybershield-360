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
    // 1. On ajoute le service d'analyse
    private final SecurityAnalyzerService securityAnalyzerService;

    private final CompliancePolicy compliancePolicy = new CompliancePolicy();

    // 2. On met à jour le constructeur pour l'injection de dépendance
    public EnrollDeviceService(DeviceRepository deviceRepository,
                               DeviceEventPublisher eventPublisher,
                               SecurityAnalyzerService securityAnalyzerService) {
        this.deviceRepository = deviceRepository;
        this.eventPublisher = eventPublisher;
        this.securityAnalyzerService = securityAnalyzerService;
    }

    @Override
    public Device enroll(String macAddress, String ipAddress, DeviceType type,
                         OsType osType, String osVersion, String hostname,
                         String vendor, Integer ttl, String openPorts) {

        // 1. Anti-doublon
        if (deviceRepository.findByMacAddress(macAddress).isPresent()) {
            throw new IllegalStateException("L'appareil avec l'adresse MAC " + macAddress + " est déjà enregistré.");
        }

        // 2. Création de l'entité
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

        // 4. Analyse intelligente de sécurité (Expert Cyber gratuit)
        // On génère le rapport et on l'injecte dans l'objet avant de sauvegarder
        String securityReport = securityAnalyzerService.analyzeDeviceSecurity(newDevice);
        newDevice.setSecurityRecommendation(securityReport);

        // 5. Persistance
        Device savedDevice = deviceRepository.save(newDevice);

        // 6. Notification (Redis)
        eventPublisher.publishDeviceCreated(savedDevice);

        return savedDevice;
    }
}