package com.fitness.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity  // Enabled security
public class SecurityConfig {
    // Now exposing securitywebfilterchain bean after creating it
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http)
    {
        //Here I will define my security configuration
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)  // The need of csrf being disabled
                .authorizeExchange(exchange -> exchange.anyExchange().authenticated()) // Authenticating the incomming requests
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())) //Setting oauth2 with a default configuration , this is the gateway
                .build();
    }
}
