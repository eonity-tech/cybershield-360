package com.cybershield.protection.adapter.out.persistence;

import com.cybershield.protection.core.domain.Software;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "softwares")
@Data
public class SoftwareEntity {

    @Id
    private UUID id;

    @Column(name = "device_id", nullable = false)
    private UUID deviceId;

    private String name;
    private String version;
    private String publisher;

    @Column(name = "is_running")
    private boolean isRunning;

    @Column(name = "critical_score")
    private Double criticalScore;

    // 1. Constructeur par défaut (Obligatoire pour JPA/Hibernate)
    public SoftwareEntity() {}

    // 2. Méthodes de conversion (Mappers)
    public static SoftwareEntity fromDomain(Software software) {
        SoftwareEntity entity = new SoftwareEntity();
        entity.setId(software.getId());
        entity.setDeviceId(software.getDeviceId());
        entity.setName(software.getName());
        entity.setVersion(software.getVersion());
        entity.setPublisher(software.getPublisher());
        entity.setRunning(software.isRunning());
        entity.setCriticalScore(software.getCriticalScore());
        return entity;
    }

    public Software toDomain() {
        return new Software(id, deviceId, name, version, publisher, isRunning);
    }

    // --- GETTERS ET SETTERS ---
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getDeviceId() { return deviceId; }
    public void setDeviceId(UUID deviceId) { this.deviceId = deviceId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public boolean isRunning() { return isRunning; }
    public void setRunning(boolean running) { isRunning = running; }

    public Double getCriticalScore() { return criticalScore; }
    public void setCriticalScore(Double criticalScore) { this.criticalScore = criticalScore; }
}