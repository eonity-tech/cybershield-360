package com.cybershield.protection.adapter.out.persistence.software;

import com.cybershield.protection.adapter.out.persistence.SpringDataSoftwareRepository;
import com.cybershield.protection.core.domain.Software;
import com.cybershield.protection.core.port.out.SoftwareRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class SoftwarePersistenceAdapter implements SoftwareRepository {

    private final SpringDataSoftwareRepository repository;

    //  On injecte le repository Spring Data via le constructeur
    public SoftwarePersistenceAdapter(SpringDataSoftwareRepository repository) {
        this.repository = repository;
    }

    // Sauvegarder ou mettre à jour un logiciel
    @Override
    public Software save(Software software) {
        SoftwareEntity entity = SoftwareEntity.fromDomain(software);
        return repository.save(entity).toDomain();
    }

    //  Pour lister les logiciels d'un PC spécifique
    @Override
    public List<Software> findByDeviceId(UUID deviceId) {
        return repository.findByDeviceId(deviceId).stream()
                .map(SoftwareEntity::toDomain)
                .toList();
    }

    //  Pour lister tous les logiciels (tous PCs confondus)
    @Override
    public List<Software> findAll() {
        return repository.findAll().stream()
                .map(SoftwareEntity::toDomain)
                .toList();
    }

    //  On utilise Optional pour gérer l'absence de résultat
    @Override
    public Optional<Software> findByDeviceIdAndName(UUID deviceId, String name) {
        return repository.findByDeviceIdAndName(deviceId, name)
                .map(SoftwareEntity::toDomain);
    }
}