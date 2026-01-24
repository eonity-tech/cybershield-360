package com.cybershield.protection.adapter.out.persistence.network;

import com.cybershield.protection.adapter.out.persistence.SpringDataNetworkRepository;
import com.cybershield.protection.core.domain.NetworkMetric;
import com.cybershield.protection.core.port.out.NetworkRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class NetworkPersistenceAdapter implements NetworkRepository {

    private final SpringDataNetworkRepository repository;

    public NetworkPersistenceAdapter(SpringDataNetworkRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(NetworkMetric metric) {
        // 1. On cherche l'entité existante la plus récente pour ce deviceId
        Optional<NetworkMetricEntity> existingEntityOpt =
                repository.findFirstByDeviceIdOrderByTimestampDesc(metric.getDeviceId());

        if (existingEntityOpt.isPresent()) {
            // --- CAS 1 : MISE À JOUR (UPDATE) ---
            // On récupère l'entité existante
            NetworkMetricEntity entityToUpdate = existingEntityOpt.get();

            // On met à jour ses valeurs
            entityToUpdate.setBytesSent(metric.getBytesSent());
            entityToUpdate.setBytesReceived(metric.getBytesReceived());

            // On met à jour le timestamp
            entityToUpdate.setTimestamp(metric.getTimestamp() != null ? metric.getTimestamp() : LocalDateTime.now());

            // Spring Data JPA va détecter que c'est une mise à jour grâce à l'ID déjà présent
            repository.save(entityToUpdate);

        } else {
            // --- CAS 2 : CRÉATION (INSERT) ---
            // C'est la première fois qu'on voit ce PC
            NetworkMetricEntity newEntity = NetworkMetricEntity.fromDomain(metric);
            repository.save(newEntity);
        }
    }

    @Override
    public NetworkMetric findLatestByDeviceId(java.util.UUID deviceId) {
        return repository.findFirstByDeviceIdOrderByTimestampDesc(deviceId)
                .map(NetworkMetricEntity::toDomain)
                .orElse(null);
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
                .map(NetworkMetricEntity::toDomain)
                .collect(Collectors.toList());
    }
}