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

        // CORS preflight
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        // Public authentication APIs
        if (path.equals("/api/login")
                || path.equals("/api/register")) {
            return true;
        }

        // Public GET job APIs
        // IMPORTANT:
        // POST /api/jobs will NOT be skipped.
        // Therefore recruiter authentication will still work for creating jobs.
        if ("GET".equalsIgnoreCase(method)
                && (path.equals("/api/jobs")
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

        // No Authorization header
        if (authHeader == null
                || !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {

            // Invalid token
            if (!jwtService.isTokenValid(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            String email = jwtService.extractEmail(token);
            String role = jwtService.extractRole(token);

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
            }

        } catch (Exception e) {

            System.err.println(
                    "JWT Error: " + e.getMessage()
            );
        }

        filterChain.doFilter(request, response);
    }
}

