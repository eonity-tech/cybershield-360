package com.cybershield.protection.core.port.out;

import com.cybershield.protection.core.domain.OrganizationPolicy;

import java.util.Optional;
import java.util.UUID;

public interface OrganizationPolicyRepository {

    /**
     * Sauvegarde ou met à jour la politique de sécurité de l'organisation.
     * @param policy L'objet politique à persister.
     * @return La politique sauvegardée (avec son ID si nouveau).
     */
    OrganizationPolicy save(OrganizationPolicy policy);

    /**
     * Récupère une politique par son identifiant unique.
     * @param id L'UUID de la politique.
     * @return Un Optional contenant la politique si trouvée.
     */
    Optional<OrganizationPolicy> findById(UUID id);

    /**
     * Récupère la dernière politique active définie (utile si on gère une seule config globale).
     * @return La politique actuelle de l'entreprise.
     */
    Optional<OrganizationPolicy> findCurrentPolicy();

    boolean existsByCompanyName(String companyName);
}