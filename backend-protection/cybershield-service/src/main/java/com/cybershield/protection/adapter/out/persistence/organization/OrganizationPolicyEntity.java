package com.cybershield.protection.adapter.out.persistence.organization;

import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "organization_policies")
public class OrganizationPolicyEntity {

    @Id
    private UUID id;

    private String companyName;
    private int totalEmployees;
    private int trainedEmployees;
    private int adminCount;

    private LocalTime workDayStart;
    private LocalTime workDayEnd;

    // On stocke la liste sous forme de texte "MAC1,MAC2,MAC3" pour simplifier
    @Column(length = 4096)
    private String criticalMacAddresses;

    public OrganizationPolicyEntity() {
    }

    public OrganizationPolicyEntity(UUID id, String companyName, int totalEmployees, int trainedEmployees, int adminCount, LocalTime workDayStart, LocalTime workDayEnd, String criticalMacAddresses) {
        this.id = id;
        this.companyName = companyName;
        this.totalEmployees = totalEmployees;
        this.trainedEmployees = trainedEmployees;
        this.adminCount = adminCount;
        this.workDayStart = workDayStart;
        this.workDayEnd = workDayEnd;
        this.criticalMacAddresses = criticalMacAddresses;
    }

    // Getters & Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCompanyName() { return companyName; }
    public int getTotalEmployees() { return totalEmployees; }
    public int getTrainedEmployees() { return trainedEmployees; }
    public int getAdminCount() { return adminCount; }
    public LocalTime getWorkDayStart() { return workDayStart; }
    public LocalTime getWorkDayEnd() { return workDayEnd; }
    public String getCriticalMacAddresses() { return criticalMacAddresses; }
}