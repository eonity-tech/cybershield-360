package com.cybershield.protection.adapter.out.persistence.device;

import com.cybershield.protection.adapter.out.persistence.SpringDataDeviceRepository;
import com.cybershield.protection.core.domain.Device;
import com.cybershield.protection.core.port.out.DeviceRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class DevicePersistenceAdapter implements DeviceRepository {

    private final SpringDataDeviceRepository springRepository;

    public DevicePersistenceAdapter(SpringDataDeviceRepository springRepository) {
        this.springRepository = springRepository;
    }

    @Override
    public Optional<Device> findById(UUID id) {
        // On cherche l'entité par son ID technique (UUID)
        return springRepository.findById(id)
                .map(this::toDomain); // On transforme l'entité trouvée en objet métier
    }

    @Override
    public List<Device> findAll() {
        // On appelle le repository Spring Data pour avoir toutes les entités
        // Puis on transforme chaque entité JPA en objet du Domaine via toDomain
        return springRepository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return springRepository.existsById(id);
    }

    @Override
    public Device save(Device device) {
        // 1. MAPPING : Domaine -> Entity JPA
        DeviceEntity entity = toEntity(device);

        // 2. ACTION : Sauvegarde en BDD
        DeviceEntity savedEntity = springRepository.save(entity);

        // 3. MAPPING : Entity JPA -> Domaine
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Device> findByMacAddress(String macAddress) {
        return springRepository.findByMacAddress(macAddress)
                .map(this::toDomain);
    }

    // --- MAPPERS PRIVÉS  ---
    private DeviceEntity toEntity(Device domain) {
        DeviceEntity entity = new DeviceEntity();
        entity.setId(domain.getId());
        entity.setMacAddress(domain.getMacAddress());
        entity.setIpAddress(domain.getIpAddress());
        entity.setType(domain.getType());
        entity.setOsType(domain.getOsType());
        entity.setOsVersion(domain.getOsVersion());
        entity.setHostname(domain.getHostname());
        entity.setVendor(domain.getVendor());
        entity.setTtl(domain.getTtl());
        entity.setOpenPorts(domain.getOpenPorts());
        entity.setStatus(domain.getStatus().name());
        entity.setEnrolledAt(domain.getEnrolledAt());
        return entity;
    }

    private Device toDomain(DeviceEntity entity) {
        // Reconstruction de l'objet métier avec les 10 paramètres (ID + les 9 du scan)
        Device domain = new Device(
                entity.getId(),
                entity.getMacAddress(),
                entity.getIpAddress(),
                entity.getType(),
                entity.getOsType(),
                entity.getOsVersion(),
                entity.getHostname(),
                entity.getVendor(),
                entity.getTtl(),
                entity.getOpenPorts()
        );

        // On restaure le statut exact
        if ("PROTECTED".equals(entity.getStatus())) {
            domain.markAsProtected();
        } else if ("COMPROMISED".equals(entity.getStatus())) {
            domain.markAsCompromised();
        }

        return domain;
    }
}