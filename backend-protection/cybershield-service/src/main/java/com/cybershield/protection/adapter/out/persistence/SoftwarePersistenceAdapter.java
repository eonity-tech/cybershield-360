package com.cybershield.protection.adapter.out.persistence;

import com.cybershield.protection.core.domain.Software;
import com.cybershield.protection.core.port.out.SoftwareRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class SoftwarePersistenceAdapter implements SoftwareRepository {
    private final SpringDataSoftwareRepository repository;

    public SoftwarePersistenceAdapter(SpringDataSoftwareRepository repository) {
        this.repository = repository;
    }

    @Override
    public Software save(Software software) {
        SoftwareEntity entity = SoftwareEntity.fromDomain(software);
        return repository.save(entity).toDomain();
    }

    @Override
    public List<Software> findByDeviceId(UUID deviceId) {
        return repository.findByDeviceId(deviceId).stream()
                .map(SoftwareEntity::toDomain)
                .toList();
    }
}