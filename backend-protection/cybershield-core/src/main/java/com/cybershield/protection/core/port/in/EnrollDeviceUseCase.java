package com.cybershield.protection.core.port.in;

import com.cybershield.protection.core.domain.Device;
import com.cybershield.protection.core.domain.DeviceType;
import com.cybershield.protection.core.domain.OsType;

public interface EnrollDeviceUseCase {
    Device enroll(String macAddress, String ipAddress, DeviceType type,
                  OsType osType, String osVersion, String hostname,
                  String vendor, Integer ttl, String openPorts);
}