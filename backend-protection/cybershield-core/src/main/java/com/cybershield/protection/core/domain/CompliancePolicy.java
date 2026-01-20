package com.cybershield.protection.core.domain;

import com.cybershield.protection.core.domain.type.DeviceType;
import com.cybershield.protection.core.domain.type.OsType;

public class CompliancePolicy {

    public void validate(Device device) {
        // Règle 1 : Interdire les OS inconnus (trop risqué)
        if (device.getOsType() == OsType.UNKNOWN) {
            throw new ComplianceException("Sécurité : Les appareils avec un OS 'UNKNOWN' sont interdits.");
        }

        // Règle 2 : Un serveur doit obligatoirement avoir une version d'OS renseignée
        if (device.getType() == DeviceType.SERVER && "N/A".equals(device.getOsVersion())) {
            throw new ComplianceException("Sécurité : Un serveur doit spécifier sa version d'OS pour l'analyse des failles.");
        }
    }

    // Exception métier
    public static class ComplianceException extends RuntimeException {
        public ComplianceException(String message) { super(message); }
    }
}