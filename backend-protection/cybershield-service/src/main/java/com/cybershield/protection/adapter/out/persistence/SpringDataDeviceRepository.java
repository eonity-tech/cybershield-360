package com.cybershield.protection.adapter.out.persistence;

import com.cybershield.protection.adapter.out.persistence.device.DeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataDeviceRepository extends JpaRepository<DeviceEntity, UUID> {
    Optional<DeviceEntity> findByMacAddress(String macAddress);
}