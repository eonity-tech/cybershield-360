package com.cybershield.protection.core.port.in;

import com.cybershield.protection.core.domain.OrganizationPolicy;
import java.time.LocalTime;
import java.util.List;

public interface DefineOrganizationPolicyUseCase {
    OrganizationPolicy definePolicy(
            String companyName,
            int totalEmployees,
            int trainedEmployees,
            int adminCount,
            LocalTime startHour,
            LocalTime endHour,
            List<String> criticalMacAddresses
    );
}