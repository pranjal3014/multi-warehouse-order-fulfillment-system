package com.fulfillment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fulfillment.filter.RateLimitFilter;
import com.fulfillment.filter.UserAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserAuthenticationFilter userAuthenticationFilter;
    private final RateLimitFilter rateLimitFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(authorize -> authorize

                        // Public APIs
                        .requestMatchers(
                                "/auth/graphql",
                                "/actuator/health",
                                "/actuator/info",
                                "/error"
                        ).permitAll()

                        // Everything else requires authentication
                        .anyRequest().authenticated())

                .addFilterBefore(rateLimitFilter,
                        UsernamePasswordAuthenticationFilter.class)

                .addFilterBefore(userAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)

                .build();
    }
}
