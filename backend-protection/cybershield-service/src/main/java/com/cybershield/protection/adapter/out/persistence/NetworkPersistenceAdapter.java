package com.cybershield.protection.adapter.out.persistence;

import com.cybershield.protection.core.domain.NetworkMetric;
import com.cybershield.protection.core.port.out.NetworkRepository;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component // <-- Très important pour que Spring le trouve !
public class NetworkPersistenceAdapter implements NetworkRepository {

    private final SpringDataNetworkRepository repository;

    public NetworkPersistenceAdapter(SpringDataNetworkRepository repository) {
        this.repository = repository;
    }

    @Override
    public NetworkMetric findLatestByDeviceId(java.util.UUID deviceId) {
        // On utilise une méthode personnalisée du Spring Data Repository
        // qui trie par date et ne prend que le premier résultat.
        return repository.findFirstByDeviceIdOrderByTimestampDesc(deviceId)
                .map(NetworkMetricEntity::toDomain)
                .orElse(null); // Retourne null si aucun flux n'est encore enregistré
    }


    @Override
    public void save(NetworkMetric metric) {
        NetworkMetricEntity entity = NetworkMetricEntity.fromDomain(metric);
        repository.save(entity);
    }

    @Override
    public List<NetworkMetric> findByDeviceId(java.util.UUID deviceId) {
        // Logique à implémenter plus tard si besoin
        return List.of();
    }

    @Override
    public List<NetworkMetric> findAllRecent() {
        return repository.findAll().stream()
                .map(NetworkMetricEntity::toDomain)
                .collect(Collectors.toList());
    }
}