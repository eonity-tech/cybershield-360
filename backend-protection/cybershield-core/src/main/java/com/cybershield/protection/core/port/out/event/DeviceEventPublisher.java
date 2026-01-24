package com.cybershield.protection.core.port.out.event;

import com.cybershield.protection.core.domain.Device;
import java.util.UUID;

public interface DeviceEventPublisher {
    // Événement existant
    void publishDeviceCreated(Device device);

    // Evénement de quarantaine
    void publishQuarantineAlert(UUID deviceId, String reason, double riskScore);
}