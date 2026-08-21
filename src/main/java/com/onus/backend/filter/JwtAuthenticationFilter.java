
        package com.onus.backend.filter;

import com.onus.backend.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getServletPath();
        String method = request.getMethod();

        /*
         * ---------------------------------------------------------
         * CORS PREFLIGHT
         * ---------------------------------------------------------
         */
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        /*
         * ---------------------------------------------------------
         * PUBLIC AUTH APIs
         * ---------------------------------------------------------
         */
        if ("/api/login".equals(path)
                || "/api/register".equals(path)) {
            return true;
        }

        /*
         * ---------------------------------------------------------
         * PUBLIC JOB APIs
         *
         * GET /api/jobs
         * GET /api/jobs/1
         * GET /api/jobs/anything
         *
         * These endpoints are public.
         * POST /api/jobs is NOT skipped.
         * ---------------------------------------------------------
         */
        if ("GET".equalsIgnoreCase(method)
                && ("/api/jobs".equals(path)
                || path.startsWith("/api/jobs/"))) {
            return true;
        }

        return false;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        /*
         * ---------------------------------------------------------
         * NO TOKEN
         *
         * Do not immediately return 403 here.
         * Spring Security will decide whether the endpoint
         * requires authentication.
         * ---------------------------------------------------------
         */
        if (authHeader == null
                || !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7).trim();

        /*
         * Empty token
         */
        if (token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {

            /*
             * -----------------------------------------------------
             * VALIDATE JWT
             * -----------------------------------------------------
             */
            if (!jwtService.isTokenValid(token)) {
                System.err.println("JWT Error: Invalid token");
                filterChain.doFilter(request, response);
                return;
            }

            /*
             * -----------------------------------------------------
             * EXTRACT USER INFORMATION
             * -----------------------------------------------------
             */
            String email = jwtService.extractEmail(token);
            String role = jwtService.extractRole(token);

            /*
             * -----------------------------------------------------
             * CREATE AUTHENTICATION
             * -----------------------------------------------------
             */
            if (email != null
                    && !email.isBlank()
                    && role != null
                    && !role.isBlank()
                    && SecurityContextHolder
                    .getContext()
                    .getAuthentication() == null) {

                SimpleGrantedAuthority authority =
                        new SimpleGrantedAuthority(role);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                List.of(authority)
                        );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);

                System.out.println(
                        "JWT authenticated user: "
                                + email
                                + " | role: "
                                + role
                );
            }

        } catch (Exception e) {

            /*
             * -----------------------------------------------------
             * JWT FAILURE
             *
             * Do not manually send 403.
             * Let Spring Security handle protected endpoints.
             * -----------------------------------------------------
             */
            System.err.println(
                    "JWT Error: " + e.getMessage()
            );
        }

        filterChain.doFilter(request, response);
    }
}

