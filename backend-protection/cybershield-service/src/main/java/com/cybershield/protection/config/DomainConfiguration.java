package com.cybershield.protection.config;

import com.cybershield.protection.core.port.in.EnrollDeviceUseCase;
import com.cybershield.protection.core.port.out.DeviceRepository;
import com.cybershield.protection.core.port.out.event.DeviceEventPublisher; // <--- IMPORT
import com.cybershield.protection.core.usecase.EnrollDeviceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfiguration {

    @Bean
    public EnrollDeviceUseCase enrollDeviceUseCase(
            DeviceRepository deviceRepository,
            DeviceEventPublisher deviceEventPublisher
    ) {
        return new EnrollDeviceService(deviceRepository, deviceEventPublisher);
    }
}