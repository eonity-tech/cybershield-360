package com.cybershield.protection.core.usecase;

import com.cybershield.protection.core.domain.Device;
import com.cybershield.protection.core.domain.type.OsType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SecurityAnalyzerService {

    // 1. On injecte le port de sortie (Interface)
    private final com.cybershield.protection.core.port.out.event.DeviceEventPublisher eventPublisher;

    public SecurityAnalyzerService(com.cybershield.protection.core.port.out.event.DeviceEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    // 2. Méthode principale d'analyse de sécurité
    public String analyzeDeviceSecurity(Device device) {
        List<String> findings = new ArrayList<>();
        double riskScore = 0.0;

        String ports = (device.getOpenPorts() != null) ? device.getOpenPorts() : "";
        String osVersion = (device.getOsVersion() != null) ? device.getOsVersion().toLowerCase() : "";

        // --- 1. ANALYSE DES PORTS ET CALCUL DU SCORE ---
        if (ports.contains("3389")) {
            findings.add("(CRITIQUE) Port RDP (3389) ouvert.");
            riskScore += 50.0;
        }
        if (ports.contains("23")) {
            findings.add("(DANGER) Port Telnet (23) détecté.");
            riskScore += 40.0;
        }
        if (ports.contains("445") && device.getOsType() == OsType.WINDOWS) {
            findings.add("(URGENT) Protocole SMB (445) exposé.");
            riskScore += 30.0;
        }

        // --- 2. DÉCLENCHEMENT DE LA QUARANTAINE (Action Proactive) ---
        // Dépassement du seuil critique 80
        if (riskScore >= 80.0) {
            eventPublisher.publishQuarantineAlert(
                    device.getId(),
                    "Score de risque élevé : " + riskScore + ". Menaces détectées : " + String.join(", ", findings),
                    riskScore
            );
        }

        // --- 3. SYNTHÈSE POUR LE DASHBOARD ---
        if (findings.isEmpty()) {
            return "Appareil sain. Aucune vulnérabilité critique détectée sur les ports analysés.";
        } else {
            return "ALERTES SÉCURITÉ : " + String.join(" | ", findings);
        }
    }
}