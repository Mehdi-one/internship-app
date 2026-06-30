package com.example.internshipapp.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private static final String DIRIGEANT = "DIRIGEANT";
    private static final String CONDUCTEUR = "CONDUCTEUR";
    private static final String GESTIONNAIRE = "GESTIONNAIRE";
    private static final String ADMIN = "ADMIN";

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/projects/*/summary")
                        .hasAnyRole(DIRIGEANT, ADMIN)

                        .requestMatchers(HttpMethod.PATCH, "/api/projects/*/close", "/api/projects/*/archive")
                        .hasAnyRole(DIRIGEANT, ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/projects", "/api/projects/*/lots")
                        .hasAnyRole(DIRIGEANT, CONDUCTEUR, ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/api/projects/*")
                        .hasAnyRole(DIRIGEANT, CONDUCTEUR, ADMIN)
                        .requestMatchers(HttpMethod.PATCH, "/api/project-lots/*/archive")
                        .hasAnyRole(DIRIGEANT, CONDUCTEUR, ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/projects/**", "/api/project-lots/**")
                        .hasAnyRole(DIRIGEANT, CONDUCTEUR, GESTIONNAIRE, ADMIN)

                        .requestMatchers(HttpMethod.POST, "/api/projects/*/expenses", "/api/expenses/*/documents")
                        .hasAnyRole(CONDUCTEUR, GESTIONNAIRE, ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/api/expenses/**")
                        .hasAnyRole(CONDUCTEUR, GESTIONNAIRE, ADMIN)
                        .requestMatchers(HttpMethod.PATCH, "/api/expenses/**")
                        .hasAnyRole(CONDUCTEUR, GESTIONNAIRE, ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/expenses/**", "/api/projects/*/expenses", "/api/expense-documents/**")
                        .hasAnyRole(DIRIGEANT, CONDUCTEUR, GESTIONNAIRE, ADMIN)

                        .requestMatchers(HttpMethod.POST, "/api/projects/*/employee-assignments", "/api/projects/*/equipment-assignments")
                        .hasAnyRole(CONDUCTEUR, ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/api/employee-assignments/**", "/api/equipment-assignments/**")
                        .hasAnyRole(CONDUCTEUR, ADMIN)
                        .requestMatchers(HttpMethod.PATCH, "/api/employee-assignments/**", "/api/equipment-assignments/**")
                        .hasAnyRole(CONDUCTEUR, ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/projects/*/employee-assignments", "/api/projects/*/equipment-assignments")
                        .hasAnyRole(DIRIGEANT, CONDUCTEUR, GESTIONNAIRE, ADMIN)

                        .requestMatchers(HttpMethod.POST, "/api/employees/**", "/api/equipment/**")
                        .hasAnyRole(GESTIONNAIRE, ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/api/employees/**", "/api/equipment/**")
                        .hasAnyRole(GESTIONNAIRE, ADMIN)
                        .requestMatchers(HttpMethod.PATCH, "/api/employees/**", "/api/equipment/**")
                        .hasAnyRole(GESTIONNAIRE, ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/employees/**", "/api/equipment/**")
                        .hasAnyRole(DIRIGEANT, CONDUCTEUR, GESTIONNAIRE, ADMIN)

                        .anyRequest().hasRole(ADMIN))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .build();
    }

    @Bean
    Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter scopeConverter = new JwtGrantedAuthoritiesConverter();
        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();

        authenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>(scopeConverter.convert(jwt));
            Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");

            if (realmAccess != null && realmAccess.get("roles") instanceof List<?> roles) {
                roles.stream()
                        .map(String::valueOf)
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .forEach(authorities::add);
            }

            return authorities;
        });

        return authenticationConverter;
    }
}
