package com.cybershield.protection.adapter.in.rest.dto;

import com.cybershield.protection.core.domain.Device;

public record DeviceResponse(
        String id,
        String macAddress,
        String ipAddress,
        String type,
        String osType,
        String osVersion,
        String hostname,
        String vendor,
        Integer ttl,
        String openPorts,
        String status,
        String enrolledAt,
        Double riskScore,
        String securityRecommendation
) {
    public static DeviceResponse fromDomain(Device device) {
        return new DeviceResponse(
                device.getId().toString(),
                device.getMacAddress(),
                device.getIpAddress(),
                device.getType().name(),
                device.getOsType().name(),
                device.getOsVersion(),
                device.getHostname(),
                device.getVendor(),
                device.getTtl(),
                device.getOpenPorts(),
                device.getStatus().name(),
                device.getEnrolledAt().toString(),
                device.calculateRiskScore(),
                device.getSecurityRecommendation()
        );
    }
}