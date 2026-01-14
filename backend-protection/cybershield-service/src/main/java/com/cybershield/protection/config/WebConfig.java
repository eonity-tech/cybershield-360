package com.cybershield.protection.config;

import com.cybershield.protection.adapter.infrastructure.jwt.JwtConnectedUserResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;

@Configuration
public class WebConfig implements WebFluxConfigurer {

    private final JwtConnectedUserResolver jwtConnectedUserResolver;

    public WebConfig(JwtConnectedUserResolver jwtConnectedUserResolver) {
        this.jwtConnectedUserResolver = jwtConnectedUserResolver;
    }

    @Override
    public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
        configurer.addCustomResolver(jwtConnectedUserResolver);
    }
}