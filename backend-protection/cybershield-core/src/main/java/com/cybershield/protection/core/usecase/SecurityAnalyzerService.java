package com.cybershield.protection.core.usecase;

import com.cybershield.protection.core.domain.Device;
import com.cybershield.protection.core.domain.type.OsType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SecurityAnalyzerService {


    // Analyse un appareil et retourne une liste de recommandations de sécurité.
    public String analyzeDeviceSecurity(Device device) {
        List<String> findings = new ArrayList<>();

        // Sécurité anti-NullPointerException
        String ports = (device.getOpenPorts() != null) ? device.getOpenPorts() : "";
        String osVersion = (device.getOsVersion() != null) ? device.getOsVersion().toLowerCase() : "";

        // --- 1. ANALYSE DES PORTS (Surface d'attaque) ---

        // RDP (3389) : Le grand classique des attaques Ransomware
        if (ports.contains("3389")) {
            if (device.getOsType() == OsType.WINDOWS) {
                findings.add("CRITIQUE : Port RDP (3389) ouvert. Risque élevé d'intrusion (Ransomware).");
            } else {
                findings.add("SUSPECT : Port 3389 ouvert sur un système non-Windows.");
            }
        }

        // Telnet (23) : Obsolète
        if (ports.contains("23")) {
            findings.add("DANGER : Port Telnet (23) détecté. Protocole non chiffré, mot de passe interceptable.");
        }

        // FTP (21) : Souvent non sécurisé
        if (ports.contains("21")) {
            findings.add("AVERTISSEMENT : Serveur FTP (21) détecté. Préférez SFTP (22).");
        }

        // SMB (445) : Vecteur de propagation (WannaCry)
        if (ports.contains("445") && device.getOsType() == OsType.WINDOWS) {
            findings.add("URGENT : Protocole SMB (445) exposé. Vérifiez les correctifs de sécurité (MS17-010).");
        }

        // --- 2. ANALYSE DU SYSTÈME (Obsolescence) ---

        if (device.getOsType() == OsType.WINDOWS) {
            if (osVersion.contains("xp") || osVersion.contains("7") || osVersion.contains("vista") ||
                    osVersion.contains("2003") || osVersion.contains("2008")) {
                findings.add("CRITIQUE : OS en fin de vie (" + device.getOsVersion() + "). Plus de mises à jour de sécurité.");
            }
        }

        // --- 3. SYNTHÈSE ---

        if (findings.isEmpty()) {
            return "Appareil sain. Aucune vulnérabilité critique détectée sur les ports analysés.";
        } else {
            return "ALERTES SÉCURITÉ : " + String.join(" | ", findings);
        }
    }
}