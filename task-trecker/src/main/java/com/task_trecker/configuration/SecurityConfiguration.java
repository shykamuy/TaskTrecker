package com.task_trecker.configuration;

import com.task_trecker.model.RoleType;
import com.task_trecker.model.entitiy.User;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

import static com.task_trecker.model.RoleType.ROLE_USER;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.security", name = "type", havingValue = "db")
    public ReactiveAuthenticationManager authenticationManager(ReactiveUserDetailsService userDetailsService,
                                                               PasswordEncoder passwordEncoder) {
        var reactiveAuthenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);

        reactiveAuthenticationManager.setPasswordEncoder(passwordEncoder);

        return reactiveAuthenticationManager;
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.security", name = "type", havingValue = "db")
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, ReactiveAuthenticationManager authenticationManager) {
        return buildDefaultHttpSecurity(http)
                .authenticationManager(authenticationManager)
                .build();
    }

    private ServerHttpSecurity buildDefaultHttpSecurity(ServerHttpSecurity http) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange((auth) -> auth
                        .pathMatchers("/api/v1/public/**").permitAll()
                        .anyExchange().authenticated())
                .httpBasic(Customizer.withDefaults());
    }


}
