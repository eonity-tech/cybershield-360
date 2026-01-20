package com.cybershield.protection.adapter.out.persistence.network;

// Assure-toi que ce chemin d'import est bon (là où se trouve ton interface Repository)
import com.cybershield.protection.adapter.out.persistence.SpringDataNetworkRepository;
import com.cybershield.protection.core.domain.NetworkMetric;
import com.cybershield.protection.core.port.out.NetworkRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class NetworkPersistenceAdapter implements NetworkRepository {

    private final SpringDataNetworkRepository repository;

    public NetworkPersistenceAdapter(SpringDataNetworkRepository repository) {
        this.repository = repository;
    }

    @Override
    public NetworkMetric findLatestByDeviceId(java.util.UUID deviceId) {
        // Cette méthode dépend de ton interface SpringDataNetworkRepository
        return repository.findFirstByDeviceIdOrderByTimestampDesc(deviceId)
                .map(NetworkMetricEntity::toDomain)
                .orElse(null);
    }

    @Override
    public void save(NetworkMetric metric) {
        // Utilise la méthode statique fromDomain qu'on a écrite manuellement
        NetworkMetricEntity entity = NetworkMetricEntity.fromDomain(metric);
        repository.save(entity);
    }

    @Override
    public List<NetworkMetric> findByDeviceId(java.util.UUID deviceId) {
        return repository.findByDeviceIdOrderByTimestampDesc(deviceId).stream()
                .map(NetworkMetricEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<NetworkMetric> findAllRecent() {
        return repository.findAll().stream()
                .map(NetworkMetricEntity::toDomain) // Utilise la méthode manuelle toDomain
                .collect(Collectors.toList());
    }

}