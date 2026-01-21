package com.cybershield.protection.config;

import com.cybershield.protection.core.port.in.DefineOrganizationPolicyUseCase;
import com.cybershield.protection.core.port.in.EnrollDeviceUseCase;
import com.cybershield.protection.core.port.out.DeviceRepository;
import com.cybershield.protection.core.port.out.OrganizationPolicyRepository;
import com.cybershield.protection.core.port.out.event.DeviceEventPublisher;
import com.cybershield.protection.core.usecase.EnrollDeviceService;
import com.cybershield.protection.core.usecase.OrganizationPolicyService;
import com.cybershield.protection.core.usecase.SecurityAnalyzerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfiguration {

    @Bean
    public EnrollDeviceUseCase enrollDeviceUseCase(
            DeviceRepository deviceRepository,
            DeviceEventPublisher deviceEventPublisher,
            SecurityAnalyzerService securityAnalyzerService
    ) {
        return new EnrollDeviceService(deviceRepository, deviceEventPublisher, securityAnalyzerService);
    }

    // Define the bean for DefineOrganizationPolicyUseCase
    @Bean
    public DefineOrganizationPolicyUseCase defineOrganizationPolicyUseCase(
            OrganizationPolicyRepository repository
    ) {
        return new OrganizationPolicyService(repository);
    }
}