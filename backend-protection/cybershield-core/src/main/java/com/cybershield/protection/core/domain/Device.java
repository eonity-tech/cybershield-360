package com.cybershield.protection.core.domain;

import com.cybershield.protection.core.domain.type.DeviceType;
import com.cybershield.protection.core.domain.type.OsType;
import com.cybershield.protection.core.domain.type.VulnerabilityLevel;

import java.time.Instant;
import java.util.UUID;
import java.util.regex.Pattern;

public class Device {
    private final UUID id;
    private final String macAddress;
    private final String ipAddress;
    private final DeviceType type;
    private final OsType osType;
    private final String osVersion;

    // --- Nouveaux champs pour l'analyse réseau ---
    private final String hostname;    // Nom réseau (ex: PC-DIRECTION)
    private final String vendor;      // Fabricant (ex: Dell, Apple)
    private final Integer ttl;        // Pour deviner l'OS (64=Linux, 128=Windows)
    private final String openPorts;   // Ports détectés (ex: "80,443,22")

    private DeviceStatus status;
    private final Instant enrolledAt;

    private static final Pattern MAC_PATTERN = Pattern.compile("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$");
    private static final Pattern IP_PATTERN = Pattern.compile("^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$");

    private String securityRecommendation;

    public Device(UUID id, String macAddress, String ipAddress, DeviceType type,
                  OsType osType, String osVersion, String hostname,
                  String vendor, Integer ttl, String openPorts) {

        // Validations de base -> Sécurité critique
        if (macAddress == null || !MAC_PATTERN.matcher(macAddress).matches()) {
            throw new IllegalArgumentException("Format d'adresse MAC invalide : " + macAddress);
        }
        if (ipAddress == null || !IP_PATTERN.matcher(ipAddress).matches()) {
            throw new IllegalArgumentException("L'adresse IP est malformée ou invalide : " + ipAddress);
        }

        this.id = id;
        this.macAddress = macAddress;
        this.ipAddress = ipAddress;
        this.type = type;
        this.osType = osType;

        // Sanitization des chaînes libres -> Anti-Injection
        this.osVersion = sanitize(osVersion, 50);
        this.hostname = sanitize(hostname, 100);
        this.vendor = sanitize(vendor, 100);

        // CORRECTION ICI : On autorise la virgule pour la liste des ports
        this.openPorts = sanitize(openPorts, 255);

        // Données techniques
        this.ttl = ttl;
        this.status = DeviceStatus.UNPROTECTED;
        this.enrolledAt = Instant.now();
    }

    // Méthode utilitaire de nettoyage pour la réutilisation
    private String sanitize(String input, int maxLength) {
        if (input == null || input.isBlank()) return "Unknown";

        String cleaned = input.replaceAll("[^a-zA-Z0-9.\\- _,/]", "");

        return (cleaned.length() > maxLength) ? cleaned.substring(0, maxLength) : cleaned;
    }

    public double calculateRiskScore() {
        double score = 0.0;

        // Règle 1 : OS Critique
        if (this.osVersion.contains("Windows 7") || this.osVersion.contains("Server 2008")) {
            score += 50.0;
        }

        // Règle 2 : Ports à haut risque
        if (this.openPorts.contains("21") || this.openPorts.contains("23")) {
            score += 30.0;
        }

        // Règle 3 : Appareil non identifié
        if (this.osType == OsType.UNKNOWN) {
            score += 20.0;
        }

        return Math.min(score, 100.0);
    }

    public String getSecurityRecommendation() {
        // 1. Si un rapport d'analyse externe a été injecté, on le retourne en priorité
        if (this.securityRecommendation != null && !this.securityRecommendation.isBlank()) {
            return this.securityRecommendation;
        }

        // 2. Sinon, on garde l'ancien calcul par défaut (Fallback)
        if (calculateRiskScore() == 0) return "Appareil conforme aux standards de sécurité.";

        StringBuilder advice = new StringBuilder("Actions recommandées (Auto) : ");
        if (this.osVersion.contains("Windows 7") || this.osVersion.contains("Server 2008")) {
            advice.append("- Migrer vers une version de Windows supportée. ");
        }
        if (this.openPorts.contains("21")) advice.append("- Fermer le port FTP. ");
        if (this.openPorts.contains("23")) advice.append("- Désactiver Telnet. ");
        if (this.osType == OsType.UNKNOWN) advice.append("- Identifier l'appareil manuellement. ");

        return advice.toString();
    }

    public void setSecurityRecommendation(String securityRecommendation) {
        this.securityRecommendation = securityRecommendation;
    }


    // Calcule le niveau de vulnérabilité basé sur le score de risque
    public VulnerabilityLevel getVulnerabilityLevel() {
        return VulnerabilityLevel.fromScore(this.calculateRiskScore());
    }

    // --- Logique Métier ---
    public void markAsProtected() { this.status = DeviceStatus.PROTECTED; }
    public void markAsCompromised() { this.status = DeviceStatus.COMPROMISED; }

    // --- Getters ---
    public UUID getId() { return id; }
    public String getMacAddress() { return macAddress; }
    public String getIpAddress() { return ipAddress; }
    public DeviceType getType() { return type; }
    public OsType getOsType() { return osType; }
    public String getOsVersion() { return osVersion; }
    public String getHostname() { return hostname; }
    public String getVendor() { return vendor; }
    public Integer getTtl() { return ttl; }
    public String getOpenPorts() { return openPorts; }
    public DeviceStatus getStatus() { return status; }
    // Le getter indispensable pour la BDD
    public Instant getEnrolledAt() { return enrolledAt; }

    public enum DeviceStatus { UNPROTECTED, PROTECTED, COMPROMISED }
}