package com.cybershield.protection.adapter.out.persistence.software;

import com.cybershield.protection.core.domain.Software;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "softwares")
public class SoftwareEntity {

    @Id
    private UUID id;

    @Column(name = "device_id", nullable = false)
    private UUID deviceId;

    private String name;
    private String version;

    // 1. AJOUT DU CHAMP EN BASE
    @Column(name = "software_type")
    private String type;

    private String publisher;

    @Column(name = "is_running")
    private boolean isRunning;

    @Column(name = "critical_score")
    private Double criticalScore;

    // Constructeur par défaut (JPA)
    public SoftwareEntity() {}

    // 2. Conversion DOMAINE -> ENTITÉ (Pour sauvegarder)
    public static SoftwareEntity fromDomain(Software software) {
        SoftwareEntity entity = new SoftwareEntity();
        entity.setId(software.getId());
        entity.setDeviceId(software.getDeviceId());
        entity.setName(software.getName());
        entity.setVersion(software.getVersion());

        // AJOUT DU MAPPING DU TYPE
        entity.setType(software.getType());

        entity.setPublisher(software.getPublisher());
        entity.setRunning(software.isRunning());
        entity.setCriticalScore(software.getCriticalScore());
        return entity;
    }

    // 3. Conversion ENTITÉ -> DOMAINE (Pour lire)
    public Software toDomain() {
        Software domain = new Software(
                id,
                deviceId,
                name,
                version,
                type,
                publisher,
                isRunning
        );

        domain.setCriticalScore(this.criticalScore);
        return domain;
    }

    // --- GETTERS ET SETTERS POUR JPA ---
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getDeviceId() { return deviceId; }
    public void setDeviceId(UUID deviceId) { this.deviceId = deviceId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    // GETTER/SETTER POUR LE TYPE
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public boolean isRunning() { return isRunning; }
    public void setRunning(boolean running) { isRunning = running; }

    public Double getCriticalScore() { return criticalScore; }
    public void setCriticalScore(Double criticalScore) { this.criticalScore = criticalScore; }
}