package com.cybershield.protection.core.domain.type;

public enum DeviceType {
    WORKSTATION,    // PC Employé
    COMPUTER,
    SERVER,         // Serveur Critique
    SMARTPHONE,     // Flotte mobile
    IOT_DEVICE,     // Caméra, Capteur (Souvent vulnérable !)
    NETWORK_GEAR,   // Routeur, Switch
    UNKNOWN,        // Type non identifié)
    OTHER,          // Autre type
    IOT             // Caméra, Capteur (Souvent vulnérable !)
}