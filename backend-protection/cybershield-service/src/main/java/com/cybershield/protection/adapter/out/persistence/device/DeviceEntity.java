package com.cybershield.protection.adapter.out.persistence.device;

import com.cybershield.protection.core.domain.type.DeviceType;
import com.cybershield.protection.core.domain.type.OsType;
import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "devices")
@Data
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
    private Instant enrolledAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private DeviceType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "os_type")
    private OsType osType;

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

    // Constructeur vide requis par JPA
    public DeviceEntity() {}
}