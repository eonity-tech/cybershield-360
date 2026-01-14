package com.cybershield.protection.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataSoftwareRepository extends JpaRepository<SoftwareEntity, UUID> {

    // Pour retrouver tous les logiciels d'un PC spécifique (clé étrangère)
    List<SoftwareEntity> findByDeviceId(UUID deviceId);
}