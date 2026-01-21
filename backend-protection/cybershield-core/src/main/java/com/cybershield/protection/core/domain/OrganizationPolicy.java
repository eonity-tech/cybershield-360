package com.cybershield.protection.core.domain;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public class OrganizationPolicy {

    private UUID id;
    private String companyName;

    // --- Facteur Humain ---
    private int totalEmployees;
    private int cyberTrainedEmployees; // Combien sont formés ?
    private int adminCount; // Combien d'admins (trop d'admins = danger)

    // --- Facteur Temporel ---
    private LocalTime workDayStart; // Ex: 08:00
    private LocalTime workDayEnd;   // Ex: 19:00

    // --- Facteur Critique ---
    // Liste des adresses MAC des machines "VIP" ou "Restreintes" (Compta, RH, Admin)
    private List<String> criticalMacAddresses;

    public OrganizationPolicy(UUID id, String companyName, int totalEmployees, int cyberTrainedEmployees,
                              int adminCount, LocalTime workDayStart, LocalTime workDayEnd,
                              List<String> criticalMacAddresses) {
        this.id = id;
        this.companyName = companyName;
        this.totalEmployees = totalEmployees;
        this.cyberTrainedEmployees = cyberTrainedEmployees;
        this.adminCount = adminCount;
        this.workDayStart = workDayStart;
        this.workDayEnd = workDayEnd;
        this.criticalMacAddresses = criticalMacAddresses;
    }

    // --- Logique Métier ---

    /**
     * Vérifie si une action se déroule hors des heures de travail.
     * C'est un facteur aggravant de risque.
     */
    public boolean isOutsideWorkingHours(LocalTime timeToCheck) {
        return timeToCheck.isBefore(workDayStart) || timeToCheck.isAfter(workDayEnd);
    }

    /**
     * Calcule le score de maturité humaine (0-100).
     * Plus le score est haut, mieux l'entreprise est préparée.
     */
    public double calculateHumanMaturityScore() {
        if (totalEmployees == 0) return 0.0;

        // Ratio de gens formés
        double trainingRatio = (double) cyberTrainedEmployees / totalEmployees;

        // Pénalité si trop d'admins (> 10% de l'effectif = danger)
        double adminRatio = (double) adminCount / totalEmployees;
        double adminPenalty = (adminRatio > 0.10) ? 20.0 : 0.0;

        double score = (trainingRatio * 100) - adminPenalty;
        return Math.max(0, Math.min(100, score)); // Borné entre 0 et 100
    }

    // --- GETTERS (Nécessaires pour la persistance) ---

    public UUID getId() {
        return id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public int getTotalEmployees() {
        return totalEmployees;
    }

    public int getCyberTrainedEmployees() {
        return cyberTrainedEmployees;
    }

    public int getAdminCount() {
        return adminCount;
    }

    public LocalTime getWorkDayStart() {
        return workDayStart;
    }

    public LocalTime getWorkDayEnd() {
        return workDayEnd;
    }

    public List<String> getCriticalMacAddresses() {
        return criticalMacAddresses;
    }

    public int getTrainedEmployees() { return cyberTrainedEmployees; }
}