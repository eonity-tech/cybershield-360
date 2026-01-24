package com.cybershield.protection.core.port.in;

import java.util.UUID;

public interface BlockDeviceUseCase {
    void blockDevice(UUID deviceId);
    void unblockDevice(UUID deviceId);
}