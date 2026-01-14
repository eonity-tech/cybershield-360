package com.cybershield.protection.core.usecase;

import com.cybershield.protection.core.domain.NetworkMetric;
import com.cybershield.protection.core.dto.GlobalDeviceDashboardResponse;
import com.cybershield.protection.core.port.in.RecordNetworkTrafficUseCase;
import com.cybershield.protection.core.port.out.DeviceRepository;
import com.cybershield.protection.core.port.out.NetworkRepository; // À créer
import com.cybershield.protection.core.port.out.SoftwareRepository;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.List;

@Service
public class NetworkMetricService implements RecordNetworkTrafficUseCase {

    private final NetworkRepository networkRepository;
    private final DeviceRepository deviceRepository;     // Pour l'IP (Phase 1)
    private final SoftwareRepository softwareRepository; // Pour les logiciels (Phase 2)

    public NetworkMetricService(NetworkRepository networkRepository,
                                DeviceRepository deviceRepository,
                                SoftwareRepository softwareRepository) {
        this.networkRepository = networkRepository;
        this.deviceRepository = deviceRepository;
        this.softwareRepository = softwareRepository;
    }

    @Override
    public void record(UUID deviceId, double bytesSent, double bytesReceived) {
        NetworkMetric metric = new NetworkMetric(
                UUID.randomUUID(),
                deviceId,
                bytesSent,
                bytesReceived
        );
        networkRepository.save(metric);
    }

    // --- LA VUE RÉCAPITULATIVE POUR LE PATRON ---
    public List<GlobalDeviceDashboardResponse> getGlobalDashboard() {
        return deviceRepository.findAll().stream().map(device -> {

            // 1. Récupérer le dernier flux connu pour ce PC (Phase 3)
            NetworkMetric lastMetric = networkRepository.findLatestByDeviceId(device.getId());
            double usage = (lastMetric != null) ? (lastMetric.getBytesSent() + lastMetric.getBytesReceived()) : 0;

            // 2. Calculer le StatusCode (1: Crit, 2: Warn, 3: Safe)
            int code = (usage > 500_000_000) ? 1 : (usage > 50_000_000) ? 2 : 3;
            String msg = switch(code) {
                case 1 -> "ALERTE : Volume anormal détecté (> 500Mo).";
                case 2 -> "ATTENTION : Utilisation importante de la bande passante.";
                default -> "Trafic réseau nominal.";
            };

            // 3. Récupérer les logiciels trouvés sur ce PC (Phase 2) [cite: 41, 50]
            List<String> softwareNames = softwareRepository.findByDeviceId(device.getId())
                    .stream()
                    .map(s -> s.getName() + " (v" + s.getVersion() + ")")
                    .toList();

            // 4. On assemble tout pour le Dashboard
            return new GlobalDeviceDashboardResponse(
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