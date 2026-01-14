package com.cybershield.protection.adapter.infrastructure.jwt;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtConnectedUserResolver implements HandlerMethodArgumentResolver {

    private final JwtParser jwtParser;

    public JwtConnectedUserResolver(JwtParser jwtParser) {
        this.jwtParser = jwtParser;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // S'active UNIQUEMENT si l'argument du contrôleur est de type ConnectedUser
        return ConnectedUser.class.equals(parameter.getParameterType());
    }

    @Override
    public Mono<Object> resolveArgument(MethodParameter parameter,
                                        BindingContext bindingContext,
                                        ServerWebExchange exchange) {

        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(auth -> auth instanceof JwtAuthenticationToken)
                .map(auth -> (JwtAuthenticationToken) auth)
                .map(JwtAuthenticationToken::getToken)
                .map(jwtParser::parse);
    }
}