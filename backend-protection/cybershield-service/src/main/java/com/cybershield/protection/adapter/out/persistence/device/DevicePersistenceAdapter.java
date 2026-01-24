package com.cybershield.protection.adapter.out.persistence.device;

import com.cybershield.protection.adapter.out.persistence.SpringDataDeviceRepository;
import com.cybershield.protection.core.domain.Device;
import com.cybershield.protection.core.domain.type.DeviceType;
import com.cybershield.protection.core.domain.type.OsType;
import com.cybershield.protection.core.port.out.DeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class DevicePersistenceAdapter implements DeviceRepository {

    private static final Logger log = LoggerFactory.getLogger(DevicePersistenceAdapter.class);

    private final SpringDataDeviceRepository springRepository;

    public DevicePersistenceAdapter(SpringDataDeviceRepository springRepository) {
        this.springRepository = springRepository;
    }

    @Override
    public Optional<Device> findById(UUID id) {
        return springRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Device> findAll() {
        return springRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return springRepository.existsById(id);
    }

    @Override
    public Device save(Device device) {
        log.info("Sauvegarde du device en base : {}", device.getId());
        DeviceEntity entity = toEntity(device);
        DeviceEntity savedEntity = springRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Device> findByMacAddress(String macAddress) {
        return springRepository.findByMacAddress(macAddress).map(this::toDomain);
    }

    // --- MAPPERS PRIVÉS ---

    private DeviceEntity toEntity(Device domain) {
        DeviceEntity entity = new DeviceEntity();

        entity.setId(domain.getId());
        entity.setMacAddress(domain.getMacAddress());
        entity.setIpAddress(domain.getIpAddress());
        entity.setOsVersion(domain.getOsVersion());
        entity.setHostname(domain.getHostname());
        entity.setVendor(domain.getVendor());
        entity.setTtl(domain.getTtl());
        entity.setOpenPorts(domain.getOpenPorts());
        entity.setSecurityRecommendation(domain.getSecurityRecommendation());

        if (domain.getEnrolledAt() != null) {
            entity.setEnrolledAt(java.time.LocalDateTime.ofInstant(
                    domain.getEnrolledAt(),
                    java.time.ZoneId.systemDefault())
            );
        }

        // Mapping des enums
        if (domain.getType() != null) {
            // mapping pour gérer COMPUTER -> WORKSTATION
            if (domain.getType() == DeviceType.COMPUTER) {
                entity.setType("WORKSTATION");
            } else {
                entity.setType(domain.getType().name());
            }
        }

        if (domain.getOsType() != null) {
            entity.setOsType(domain.getOsType().name());
        }
        if (domain.getStatus() != null) {
            entity.setStatus(domain.getStatus().name());
        }

        return entity;
    }

    private Device toDomain(DeviceEntity entity) {
        DeviceType type = DeviceType.UNKNOWN;

        //
        if (entity.getType() != null) {
            // mapping pour gérer WORKSTATION -> COMPUTER
            if ("WORKSTATION".equalsIgnoreCase(entity.getType())) {
                type = DeviceType.COMPUTER;
            } else {
                try {
                    type = DeviceType.valueOf(entity.getType());
                } catch (IllegalArgumentException e) {
                    type = DeviceType.UNKNOWN;
                }
            }
        }

        OsType osType = entity.getOsType() != null ? OsType.valueOf(entity.getOsType()) : OsType.UNKNOWN;

        Device domain = new Device(
                entity.getId(),
                entity.getMacAddress(),
                entity.getIpAddress(),
                type,
                osType,
                entity.getOsVersion(),
                entity.getHostname(),
                entity.getVendor(),
                entity.getTtl(),
                entity.getOpenPorts()
        );

        domain.setSecurityRecommendation(entity.getSecurityRecommendation());

        if ("PROTECTED".equals(entity.getStatus())) {
            domain.markAsProtected();
        } else if ("COMPROMISED".equals(entity.getStatus())) {
            domain.markAsCompromised();
        }

        return domain;
    }
}