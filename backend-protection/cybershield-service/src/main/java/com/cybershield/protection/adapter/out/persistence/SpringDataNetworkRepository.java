package com.cybershield.protection.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataNetworkRepository extends JpaRepository<NetworkMetricEntity, UUID> {

    // Trouve les métriques récentes pour un PC
    List<NetworkMetricEntity> findByDeviceIdOrderByTimestampDesc(UUID deviceId);

    // AJOUT : Pour récupérer uniquement le dernier flux (StatusCode temps réel)
    Optional<NetworkMetricEntity> findFirstByDeviceIdOrderByTimestampDesc(UUID deviceId);

    // Pour le calcul global du réseau
    List<NetworkMetricEntity> findByTimestampAfter(LocalDateTime time);
}