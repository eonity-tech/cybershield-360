package com.cybershield.protection.core.usecase;

import com.cybershield.protection.core.domain.NetworkMetric;
import com.cybershield.protection.core.domain.type.VulnerabilityLevel; // N'oublie pas cet import !
import com.cybershield.protection.core.model.GlobalDeviceDashboardSummary;
import com.cybershield.protection.core.port.in.RecordNetworkTrafficUseCase;
import com.cybershield.protection.core.port.out.DeviceRepository;
import com.cybershield.protection.core.port.out.NetworkRepository;
import com.cybershield.protection.core.port.out.SoftwareRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.List;

@Service
public class NetworkMetricService implements RecordNetworkTrafficUseCase {

    private final NetworkRepository networkRepository;
    private final DeviceRepository deviceRepository;
    private final SoftwareRepository softwareRepository;

    public NetworkMetricService(NetworkRepository networkRepository,
                                DeviceRepository deviceRepository,
                                SoftwareRepository softwareRepository) {
        this.networkRepository = networkRepository;
        this.deviceRepository = deviceRepository;
        this.softwareRepository = softwareRepository;
    }

    @Override
    public void record(UUID deviceId, double bytesSent, double bytesReceived) {
        if (deviceId == null) {
            throw new IllegalArgumentException("L'identifiant de l'appareil est obligatoire.");
        }
        NetworkMetric metric = new NetworkMetric(UUID.randomUUID(), deviceId, bytesSent, bytesReceived);
        networkRepository.save(metric);
    }

    // Méthode pour obtenir le tableau de bord global
    public List<GlobalDeviceDashboardSummary> getGlobalDashboard() {
        return deviceRepository.findAll().stream().map(device -> {

            // 1. Récupérer le trafic
            NetworkMetric lastMetric = networkRepository.findLatestByDeviceId(device.getId());
            double usage = (lastMetric != null) ? (lastMetric.getBytesSent() + lastMetric.getBytesReceived()) : 0;

            // 2. Récupérer les logiciels
            List<String> softwareNames = softwareRepository.findByDeviceId(device.getId())
                    .stream()
                    .map(s -> s.getName() + " (v" + s.getVersion() + ")")
                    .toList();

            // 3. LOGIQUE DE DÉCISION
            int code;
            String msg;

            // PRIORITÉ 1 : SÉCURITÉ DES APPAREILS
            if (device.getVulnerabilityLevel() == VulnerabilityLevel.CRITICAL) {
                code = 1; // ROUGE
                msg = device.getSecurityRecommendation();
            }
            else if (device.getVulnerabilityLevel() == VulnerabilityLevel.HIGH) {
                code = 2; // ORANGE
                msg = device.getSecurityRecommendation();
            }
            // PRIORITÉ 2 : TRAFIC RÉSEAU
            else if (usage > 500_000_000) {
                code = 1;
                msg = "ALERTE : Volume anormal détecté (> 500Mo).";
            }
            else if (usage > 50_000_000) {
                code = 2;
                msg = "ATTENTION : Utilisation importante de la bande passante.";
            }
            else {
                code = 3;
                msg = "Trafic réseau nominal. Système sain.";
            }

            // 4. Assemblage
            return new GlobalDeviceDashboardSummary(
                    device.getId(),
                    device.getIpAddress(),
                    device.getHostname(),
                    softwareNames,
                    usage,
                    code,
                    msg
            );
        }).toList();
    }
}