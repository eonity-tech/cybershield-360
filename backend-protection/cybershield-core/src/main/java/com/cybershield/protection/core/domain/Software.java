package com.cybershield.protection.core.domain;

import java.util.UUID;

public class Software {
    private final UUID id;
    private final UUID deviceId;
    private final String name;
    private final String version;
    private final String publisher;
    private final boolean isRunning;
    private final Double criticalScore;

    public Software(UUID id, UUID deviceId, String name, String version, String publisher, boolean isRunning) {
        this.id = id;
        this.deviceId = deviceId;
        this.name = name;
        this.version = version;
        this.publisher = (publisher == null || publisher.isBlank()) ? "Unknown Publisher" : publisher;
        this.isRunning = isRunning;
        this.criticalScore = calculateSoftwareRisk();
    }

    // Logiciel métier : Calcul du risque lié au logiciel
    private Double calculateSoftwareRisk() {
        double score = 0.0;
        String n = name.toLowerCase();

        // Détection de vulnérabilités connues (CVE simulées)
        if (n.contains("log4j") && version.equals("2.14.1")) score += 100.0;
        if (n.contains("apache") && version.startsWith("2.4.49")) score += 90.0;

        // Logiciels à risque (Shadow IT)
        if (n.contains("teamviewer") || n.contains("anydesk")) score += 40.0;
        if (n.contains("torrent") || n.contains("u-torrent")) score += 60.0;

        return Math.min(score, 100.0);
    }

    // Logique de recommandation pour l'utilisateur
    public String getSecurityRecommendation() {
        if (criticalScore >= 90) return "DANGER : Faille critique détectée. Mettez à jour ou supprimez ce logiciel immédiatement.";
        if (criticalScore >= 40) return "AVERTISSEMENT : Logiciel de prise en main à distance ou non autorisé détecté.";
        if (criticalScore > 0) return "Note : Version ancienne détectée, une mise à jour est conseillée.";
        return "Logiciel sain.";
    }

    // --- GETTERS ---
    public UUID getId() { return id; }
    public UUID getDeviceId() { return deviceId; }
    public String getName() { return name; }
    public String getVersion() { return version; }
    public String getPublisher() { return publisher; }
    public boolean isRunning() { return isRunning; }
    public Double getCriticalScore() { return criticalScore; }
}