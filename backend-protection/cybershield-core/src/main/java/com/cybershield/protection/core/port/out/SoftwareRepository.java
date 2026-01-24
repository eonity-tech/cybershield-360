package com.cybershield.protection.core.port.out;

import com.cybershield.protection.core.domain.Software;

import java.util.List;
import java.util.Optional; // ✅ Il manquait cette ligne !
import java.util.UUID;

public interface SoftwareRepository {

    // Sauvegarder ou mettre à jour un logiciel
    Software save(Software software);

    // Pour lister les logiciels d'un PC spécifique
    List<Software> findByDeviceId(UUID deviceId);

    // Pour lister tous les logiciels (tous PCs confondus)
    List<Software> findAll();

    // Pour retrouver un logiciel par son nom et l'ID du PC et éviter les doublons
    Optional<Software> findByDeviceIdAndName(UUID deviceId, String softwareName);
}