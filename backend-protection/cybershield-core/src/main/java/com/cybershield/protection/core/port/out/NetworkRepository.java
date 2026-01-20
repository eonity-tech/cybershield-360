package com.cybershield.protection.core.port.out;

import com.cybershield.protection.core.domain.NetworkMetric;
import java.util.List;
import java.util.UUID;

public interface NetworkRepository {
    void save(NetworkMetric metric);
    List<NetworkMetric> findByDeviceId(UUID deviceId);
    List<NetworkMetric> findAllRecent();
    NetworkMetric findLatestByDeviceId(UUID deviceId);
}