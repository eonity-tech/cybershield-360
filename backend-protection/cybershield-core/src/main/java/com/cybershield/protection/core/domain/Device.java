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
    private String ipAddress;
    private final DeviceType type;
    private final OsType osType;
    private final String osVersion;

    // --- champs pour l'analyse réseau (non final) ---
    private String hostname;
    private String vendor;
    private Integer ttl;
    private String openPorts;

    private DeviceStatus status;
    private final Instant enrolledAt;

    private static final Pattern MAC_PATTERN = Pattern.compile("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$");
    private static final Pattern IP_PATTERN = Pattern.compile("^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$");

    private String securityRecommendation;

    public Device(UUID id, String macAddress, String ipAddress, DeviceType type,
                  OsType osType, String osVersion, String hostname,
                  String vendor, Integer ttl, String openPorts) {

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

        this.osVersion = sanitize(osVersion, 50);
        this.hostname = sanitize(hostname, 100);
        this.vendor = sanitize(vendor, 100);
        this.openPorts = sanitize(openPorts, 255);

        this.ttl = ttl;
        this.status = DeviceStatus.UNPROTECTED;
        this.enrolledAt = Instant.now();
    }

    private String sanitize(String input, int maxLength) {
        if (input == null || input.isBlank()) return "Unknown";
        String cleaned = input.replaceAll("[^a-zA-Z0-9.\\- _,/]", "");
        return (cleaned.length() > maxLength) ? cleaned.substring(0, maxLength) : cleaned;
    }

    public double calculateRiskScore() {
        double score = 0.0;

        // --- 1. OS Obsolètes ---
        if (this.osVersion != null) {
            if (this.osVersion.contains("Windows 7") ||
                    this.osVersion.contains("Server 2008") ||
                    this.osVersion.contains("XP")) {
                score += 60.0;
            }
        }

        // --- 2. Protocoles non chiffrés ---
        if (this.openPorts.contains("21")) score += 30.0;
        if (this.openPorts.contains("23")) score += 40.0;
        if (this.openPorts.contains("80") && !this.openPorts.contains("443")) {
            score += 10.0;
        }

        // --- 3. Administration à distance ---
        if (this.openPorts.contains("3389")) score += 100.0;
        if (this.openPorts.contains("5900")) score += 70.0;

        // --- 4. Fichiers partagés & Réseau ---
        if (this.openPorts.contains("445")) score += 100.0;

        // --- 5. Bases de données exposées ---
        if (this.openPorts.contains("3306")) score += 60.0;
        if (this.openPorts.contains("5432")) score += 60.0;
        if (this.openPorts.contains("27017")) score += 80.0;
        if (this.openPorts.contains("6379")) score += 80.0;

        // --- 6. Appareil inconnu ---
        if (this.osType == OsType.UNKNOWN) {
            score += 10.0;
        }

        return Math.min(score, 100.0);
    }

    public String getSecurityRecommendation() {
        // 1. Priorité au rapport manuel, sauf s'il est vide
        if (this.securityRecommendation != null && !this.securityRecommendation.isBlank()) {
            return this.securityRecommendation;
        }

        double score = calculateRiskScore();

        // 2. Si score nul, tout va bien
        if (score == 0) return "Appareil sain. Aucune vulnérabilité critique détectée sur les ports analysés.";

        StringBuilder advice = new StringBuilder("Actions recommandées : ");

        // --- SECTION WINDOWS / OS ---
        if (this.osVersion != null && (this.osVersion.contains("Windows 7") || this.osVersion.contains("Server 2008"))) {
            advice.append("(CRITIQUE) OS Obsolète. Migrer vers une version supportée. ");
        }

        // --- SECTION PROTOCOLES CLAIRS ---
        if (this.openPorts.contains("21")) advice.append("Fermer le port FTP (21). ");
        if (this.openPorts.contains("23")) advice.append("Désactiver Telnet (23). ");
        if (this.openPorts.contains("80") && !this.openPorts.contains("443")) {
            advice.append("Activer HTTPS (443) et rediriger le trafic HTTP (80). ");
        }

        // --- SECTION ACCÈS DISTANT ---
        if (this.openPorts.contains("3389")) advice.append("(URGENT) Restreindre RDP (3389) au VPN uniquement. ");
        if (this.openPorts.contains("5900")) advice.append("Sécuriser VNC ou utiliser un tunnel SSH. ");
        if (this.openPorts.contains("445")) advice.append("(URGENT) Bloquer SMB (445) depuis Internet (WannaCry). ");

        // --- SECTION BASES DE DONNÉES ---
        if (this.openPorts.contains("3306") || this.openPorts.contains("5432")) {
            advice.append("(DANGER) Base de données SQL exposée (3306/5432). Restreindre l'IP source. ");
        }
        if (this.openPorts.contains("6379") || this.openPorts.contains("27017")) {
            advice.append("(CRITIQUE) Base NoSQL (Redis/Mongo) exposée. Risque total de vol de données. ");
        }

        // --- Fallback ---
        if (advice.toString().equals("Actions recommandées : ")) {
            advice.append("Des ports ou configurations suspects ont été détectés. Vérifiez manuellement.");
        }

        return advice.toString();
    }

    public void setSecurityRecommendation(String securityRecommendation) {
        this.securityRecommendation = securityRecommendation;
    }

    public VulnerabilityLevel getVulnerabilityLevel() {
        return VulnerabilityLevel.fromScore(this.calculateRiskScore());
    }

    // IMPLEMENTATION COMPLETE ET INTELLIGENTE
    public void updateNetworkInfo(String ipAddress, String hostname, String vendor, Integer ttl, String openPorts) {
        // 1. Mise à jour des données avec nettoyage
        this.ipAddress = ipAddress;
        this.hostname = sanitize(hostname, 100);
        this.vendor = sanitize(vendor, 100);
        this.ttl = ttl;
        this.openPorts = sanitize(openPorts, 255);

        // 2. Réinitialisation de la recommandation pour recalcul
        this.securityRecommendation = null;
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
    public Instant getEnrolledAt() { return enrolledAt; }

    public enum DeviceStatus { UNPROTECTED, PROTECTED, COMPROMISED }
}