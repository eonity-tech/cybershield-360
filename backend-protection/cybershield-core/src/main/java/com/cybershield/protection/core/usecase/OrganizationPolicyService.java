package com.cybershield.protection.core.usecase;

import com.cybershield.protection.core.domain.OrganizationPolicy;
import com.cybershield.protection.core.exception.PolicyAlreadyExistsException;
import com.cybershield.protection.core.port.in.DefineOrganizationPolicyUseCase;
import com.cybershield.protection.core.port.out.OrganizationPolicyRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public class OrganizationPolicyService implements DefineOrganizationPolicyUseCase {

    private final OrganizationPolicyRepository repository;

    public OrganizationPolicyService(OrganizationPolicyRepository repository) {
        this.repository = repository;
    }

    @Override
    public OrganizationPolicy definePolicy(String companyName, int totalEmployees, int trainedEmployees,
                                           int adminCount, LocalTime startHour, LocalTime endHour,
                                           List<String> criticalMacAddresses) {

        if (repository.existsByCompanyName(companyName)) {
            throw new PolicyAlreadyExistsException(companyName);
        }

        // Validation simple
        if (trainedEmployees > totalEmployees) {
            throw new IllegalArgumentException("Il ne peut pas y avoir plus de formés que d'employés !");
        }

        // Création de la politique
        OrganizationPolicy policy = new OrganizationPolicy(
                UUID.randomUUID(),
                companyName,
                totalEmployees,
                trainedEmployees,
                adminCount,
                startHour,
                endHour,
                criticalMacAddresses
        );

        repository.save(policy);

        // Log simple pour démonstration
        System.out.println("Politique de sécurité définie pour : " + companyName);
        System.out.println("Score de maturité humaine : " + policy.calculateHumanMaturityScore() + "/100");

        return policy;
    }
}