package com.cybershield.protection.adapter.out.persistence.device;

import com.cybershield.protection.core.domain.Device;
import com.cybershield.protection.core.domain.type.DeviceType;
import com.cybershield.protection.core.domain.type.OsType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Entity
@Table(name = "devices")
public class DeviceEntity {

    @Id
    private UUID id;

    @Column(name = "mac_address", unique = true, nullable = false)
    private String macAddress;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "status")
    private String status;

    @Column(name = "enrolled_at")
    private LocalDateTime enrolledAt;

    @Column(name = "type")
    private String type;

    @Column(name = "os_type")
    private String osType;

    @Column(name = "os_version")
    private String osVersion;

    @Column(name = "hostname")
    private String hostname;

    @Column(name = "vendor")
    private String vendor;

    @Column(name = "ttl")
    private Integer ttl;

    @Column(name = "open_ports")
    private String openPorts;

    public DeviceEntity() {}

    // --- MAPPERS ---

    // Domaine -> Entité
    public static DeviceEntity fromDomain(Device domain) {
        DeviceEntity entity = new DeviceEntity();
        entity.setId(domain.getId());
        entity.setMacAddress(domain.getMacAddress());
        entity.setIpAddress(domain.getIpAddress());

        // Conversion Enum -> String
        entity.setType(domain.getType().name());
        entity.setOsType(domain.getOsType().name());

        entity.setOsVersion(domain.getOsVersion());
        entity.setHostname(domain.getHostname());
        entity.setVendor(domain.getVendor());
        entity.setTtl(domain.getTtl());
        entity.setOpenPorts(domain.getOpenPorts());
        entity.setStatus(domain.getStatus().name());

        // Conversion Date (Instant -> LocalDateTime UTC)
        if (domain.getEnrolledAt() != null) {
            entity.setEnrolledAt(LocalDateTime.ofInstant(domain.getEnrolledAt(), ZoneOffset.UTC));
        }

        return entity;
    }

    // Entité -> Domaine
    public Device toDomain() {
        // Reconstruction de l'objet Device
        Device device = new Device(
                this.id,
                this.macAddress,
                this.ipAddress,
                DeviceType.valueOf(this.type), // String -> Enum
                OsType.valueOf(this.osType), // String -> Enum
                this.osVersion,
                this.hostname,
                this.vendor,
                this.ttl,
                this.openPorts
        );

        // On remet le statut et la date manuellement si nécessaire (selon ton constructeur Device)
        if ("PROTECTED".equals(this.status)) device.markAsProtected();
        if ("COMPROMISED".equals(this.status)) device.markAsCompromised();

        return device;
    }

    // --- GETTERS & SETTERS ---
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getMacAddress() { return macAddress; }
    public void setMacAddress(String macAddress) { this.macAddress = macAddress; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getEnrolledAt() { return enrolledAt; }
    public void setEnrolledAt(LocalDateTime enrolledAt) { this.enrolledAt = enrolledAt; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getOsType() { return osType; }
    public void setOsType(String osType) { this.osType = osType; }
    public String getOsVersion() { return osVersion; }
    public void setOsVersion(String osVersion) { this.osVersion = osVersion; }
    public String getHostname() { return hostname; }
    public void setHostname(String hostname) { this.hostname = hostname; }
    public String getVendor() { return vendor; }
    public void setVendor(String vendor) { this.vendor = vendor; }
    public Integer getTtl() { return ttl; }
    public void setTtl(Integer ttl) { this.ttl = ttl; }
    public String getOpenPorts() { return openPorts; }
    public void setOpenPorts(String openPorts) { this.openPorts = openPorts; }
}