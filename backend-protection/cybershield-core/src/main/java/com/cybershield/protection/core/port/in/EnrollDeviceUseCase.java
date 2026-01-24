package com.cybershield.protection.core.port.in;

import com.cybershield.protection.core.domain.Device;
import com.cybershield.protection.core.domain.type.DeviceType;
import com.cybershield.protection.core.domain.type.OsType;

import java.util.UUID;

// Enregistrement d'un appareil
// Respecte la sémantique du DDD (Domain-Driven Design)
public interface EnrollDeviceUseCase {
    Device enroll(String macAddress, String ipAddress, DeviceType type,
                  OsType osType, String osVersion, String hostname,
                  String vendor, Integer ttl, String openPorts);

    void blockDevice(UUID deviceId);
    void unblockDevice(UUID deviceId);
}