package com.cybershield.protection.adapter.out.persistence.organization;

import com.cybershield.protection.adapter.out.persistence.SpringDataOrganizationPolicyRepository;
import com.cybershield.protection.core.domain.OrganizationPolicy;
import com.cybershield.protection.core.port.out.OrganizationPolicyRepository;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class OrganizationPolicyPersistenceAdapter implements OrganizationPolicyRepository {

    private final SpringDataOrganizationPolicyRepository springDataRepository;

    public OrganizationPolicyPersistenceAdapter(SpringDataOrganizationPolicyRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public boolean existsByCompanyName(String companyName) {
        return springDataRepository.existsByCompanyName(companyName);
    }

    @Override
    public OrganizationPolicy save(OrganizationPolicy policy) {
        OrganizationPolicyEntity entity = mapToEntity(policy);
        springDataRepository.save(entity);
        return policy;
    }

    @Override
    public Optional<OrganizationPolicy> findById(UUID id) {
        // La méthode mapToDomain est appelée ici pour convertir l'entité en domaine
        return springDataRepository.findById(id)
                .map(this::mapToDomain);
    }

    @Override
    public Optional<OrganizationPolicy> findCurrentPolicy() {
        return springDataRepository.findFirstByOrderByCompanyName()
                .map(this::mapToDomain);
    }

    // --- MAPPERS (Méthodes privées) ---

    private OrganizationPolicyEntity mapToEntity(OrganizationPolicy domain) {
        String macAddressString = (domain.getCriticalMacAddresses() != null)
                ? String.join(",", domain.getCriticalMacAddresses())
                : "";

        return new OrganizationPolicyEntity(
                domain.getId(),
                domain.getCompanyName(),
                domain.getTotalEmployees(),
                domain.getTrainedEmployees(),
                domain.getAdminCount(),
                domain.getWorkDayStart(),
                domain.getWorkDayEnd(),
                macAddressString
        );
    }

    // Convertit une entité en domaine
    private OrganizationPolicy mapToDomain(OrganizationPolicyEntity entity) {
        List<String> macAddresses = (entity.getCriticalMacAddresses() != null && !entity.getCriticalMacAddresses().isEmpty())
                ? Arrays.asList(entity.getCriticalMacAddresses().split(","))
                : Collections.emptyList();

        return new OrganizationPolicy(
                entity.getId(),
                entity.getCompanyName(),
                entity.getTotalEmployees(),
                entity.getTrainedEmployees(),
                entity.getAdminCount(),
                entity.getWorkDayStart(),
                entity.getWorkDayEnd(),
                macAddresses
        );
    }

}