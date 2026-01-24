package com.cybershield.protection.core.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Software {
    private final UUID id;
    private final UUID deviceId;
    private final String name;
    private String version;
    private String type;
    private final String publisher;
    private final boolean isRunning;
    private Double criticalScore;
    private LocalDateTime updatedAt;

    public Software(UUID id, UUID deviceId, String name, String version, String type, String publisher, boolean isRunning) {
        this.id = id;
        this.deviceId = deviceId;
        this.name = name;
        this.version = version;
        this.type = type;
        this.publisher = (publisher == null || publisher.isBlank()) ? "Unknown Publisher" : publisher;
        this.isRunning = isRunning;
        this.updatedAt = LocalDateTime.now();

        // Calcul initial
        this.criticalScore = calculateSoftwareRisk();
    }

    // --- Logique Métier ---

    private Double calculateSoftwareRisk() {
        double score = 0.0;
        if (name == null) return 0.0;
        String n = name.toLowerCase();
        String v = (version != null) ? version.toLowerCase() : "";

        // 1. Détection CVE critiques (Simulé)
        if (n.contains("log4j") && v.equals("2.14.1")) score += 100.0;
        if (n.contains("apache") && v.startsWith("2.4.49")) score += 90.0;

        // 2. Logiciels de Hacking / Sniffing (C'est ce qui manquait pour le test !)
        if (n.contains("wireshark") || n.contains("nmap")) score += 85.0;

        // 3. Shadow IT & Risques divers
        if (n.contains("teamviewer") || n.contains("anydesk")) score += 40.0;
        if (n.contains("torrent") || n.contains("u-torrent")) score += 60.0;

        // 4. Versions instables (Bêta/Alpha) - Souvent vulnérables
        if (v.contains("beta") || v.contains("alpha") || v.contains("rc")) score += 90.0;

        return Math.min(score, 100.0);
    }

    public String getSecurityRecommendation() {
        if (criticalScore == null) return "Analyse en cours...";
        if (criticalScore >= 90) return "(DANGER) Faille critique détectée. Mettez à jour ou supprimez ce logiciel immédiatement.";
        if (criticalScore >= 40) return "(AVERTISSEMENT) Logiciel de prise en main à distance ou non autorisé détecté.";
        if (criticalScore > 0) return "Note : Version ancienne détectée, une mise à jour est conseillée.";
        return "Logiciel sain.";
    }

    // ✅ CORRECTION ICI : Recalcul du score lors de la mise à jour
    public void updateInfo(String newVersion, String newType) {
        boolean versionChanged = !this.version.equals(newVersion);

        this.type = newType;

        if (versionChanged) {
            this.version = newVersion;
            this.updatedAt = LocalDateTime.now();

            // 🔥 IMPORTANT : Si la version change, le risque change aussi !
            this.criticalScore = calculateSoftwareRisk();
        }
    }

    // --- GETTERS ---
    public UUID getId() { return id; }
    public UUID getDeviceId() { return deviceId; }
    public String getName() { return name; }
    public String getVersion() { return version; }
    public String getType() { return type; } // Ajouté
    public String getPublisher() { return publisher; }
    public boolean isRunning() { return isRunning; }
    public Double getCriticalScore() { return criticalScore; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } // Ajouté

    // Setter pour le score critique (utile pour les tests)
    public void setCriticalScore(Double score) {
        this.criticalScore = score;
    }
}