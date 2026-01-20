package com.cybershield.protection.core.port.out;

import com.cybershield.protection.core.domain.Software;
import java.util.List;
import java.util.UUID;

public interface SoftwareRepository {
    Software save(Software software);
    List<Software> findByDeviceId(UUID deviceId);
    List<Software> findAll();
}