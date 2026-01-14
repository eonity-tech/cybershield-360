package com.cybershield.protection.core.port.out;

import com.cybershield.protection.core.domain.Device;

public interface DeviceEventPublisher {
    void publishDeviceCreated(Device device);
}