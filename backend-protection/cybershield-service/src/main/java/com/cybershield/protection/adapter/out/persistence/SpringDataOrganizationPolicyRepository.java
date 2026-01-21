package com.cybershield.protection.adapter.out.persistence;

import com.cybershield.protection.adapter.out.persistence.organization.OrganizationPolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataOrganizationPolicyRepository extends JpaRepository<OrganizationPolicyEntity, UUID> {

    // Pour vérifier les doublons
    boolean existsByCompanyName(String companyName);

    @Query(value = "SELECT * FROM organization_policies ORDER BY company_name LIMIT 1", nativeQuery = true)
    Optional<OrganizationPolicyEntity> findFirstByOrderByCompanyName();
}