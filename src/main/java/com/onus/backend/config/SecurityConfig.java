package com.onus.backend.config;

import com.onus.backend.filter.JwtAuthenticationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // =====================================================
    // PASSWORD ENCODER
    // =====================================================

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // =====================================================
    // CORS CONFIGURATION
    // =====================================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:5173",
                        "https://onus-phi.vercel.app"
                )
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of("*")
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }

    // =====================================================
    // SECURITY FILTER CHAIN
    // =====================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                // =================================================
                // CSRF
                // =================================================
                .csrf(csrf ->
                        csrf.disable()
                )

                // =================================================
                // CORS
                // =================================================
                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource()
                        )
                )

                // =================================================
                // STATELESS JWT SESSION
                // =================================================
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // =================================================
                // AUTHORIZATION
                // =================================================
                .authorizeHttpRequests(auth -> auth

                        // CORS PREFLIGHT
                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()

                        // LOGIN
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/login"
                        ).permitAll()

                        // REGISTER
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/register"
                        ).permitAll()

                        // VIEW ALL JOBS
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/jobs"
                        ).permitAll()

                        // VIEW JOB DETAILS
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/jobs/**"
                        ).permitAll()

                        // CREATE JOB - RECRUITER ONLY
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/jobs"
                        ).hasAuthority("recruiter")

                        // APPLY FOR JOB
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/applications/**"
                        ).authenticated()

                        // CHECK APPLICATION STATUS
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/applications/check/**"
                        ).authenticated()

                        // MY APPLICATIONS
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/applications/my"
                        ).authenticated()

                        // EVERYTHING ELSE
                        .anyRequest().authenticated()
                )

                // =================================================
                // JWT FILTER
                // =================================================
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}