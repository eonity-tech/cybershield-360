package com.cybershield.protection.core.port.out;

import com.cybershield.protection.core.domain.Device;
import java.util.List; // Ajoute l'import
import java.util.Optional;
import java.util.UUID;

public interface DeviceRepository {
    Device save(Device device);
    Optional<Device> findByMacAddress(String macAddress);

    // INDISPENSABLE pour getGlobalDashboard()
    List<Device> findAll();

    // Optionnel mais recommandé pour la cohérence
    Optional<Device> findById(UUID id);
}