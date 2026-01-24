package com.cybershield.protection.adapter.out.persistence;

import com.cybershield.protection.adapter.out.persistence.software.SoftwareEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataSoftwareRepository extends JpaRepository<SoftwareEntity, UUID> {

    // Pour retrouver tous les logiciels d'un PC spécifique (clé étrangère)
    List<SoftwareEntity> findByDeviceId(UUID deviceId);
    Optional<SoftwareEntity> findByDeviceIdAndName(UUID deviceId, String name);
}