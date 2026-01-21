package com.cybershield.protection.adapter.in.rest;

import com.cybershield.protection.core.domain.OrganizationPolicy;
import com.cybershield.protection.core.port.in.DefineOrganizationPolicyUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/organization/policy")
public class OrganizationPolicyController {

    private final DefineOrganizationPolicyUseCase useCase;

    public OrganizationPolicyController(DefineOrganizationPolicyUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public ResponseEntity<OrganizationPolicyResponse> definePolicy(@RequestBody @Valid PolicyRequest request) {

        OrganizationPolicy policy = useCase.definePolicy(
                request.companyName(),
                request.totalEmployees(),
                request.trainedEmployees(),
                request.adminCount(),
                request.workDayStart(),
                request.workDayEnd(),
                request.criticalMacAddresses()
        );

        return ResponseEntity.ok(new OrganizationPolicyResponse(
                policy.getId().toString(),
                "Politique mise à jour avec succès",
                policy.calculateHumanMaturityScore()
        ));
    }

    // --- DTOs Internes (Records) ---

    public record PolicyRequest(
            String companyName,
            int totalEmployees,
            int trainedEmployees,
            int adminCount,
            @NotNull LocalTime workDayStart, // Format "08:00:00"
            @NotNull LocalTime workDayEnd,   // Format "18:00:00"
            List<String> criticalMacAddresses
    ) {}

    public record OrganizationPolicyResponse(
            String policyId,
            String message,
            double humanSecurityScore // On renvoie le score calculé !
    ) {}
}