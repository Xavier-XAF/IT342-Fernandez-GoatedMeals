package edu.cit.fernandez.goatedmeals.security;

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
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        System.out.println("\n--- JWT FILTER TRIGGERED ---");
        System.out.println("Request Path: " + request.getRequestURI());

        String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization Header: " + authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            System.out.println("Token extracted successfully.");

            if (jwtUtil.validateToken(token)) {
                String email = jwtUtil.extractEmail(token);
                String role = jwtUtil.extractRole(token);
                System.out.println("Token is VALID! Email: " + email + " | Role: " + role);

                if (!role.startsWith("ROLE_")) {
                    role = "ROLE_" + role;
                }

                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        email, null, Collections.singletonList(authority)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("Successfully set Authentication in Security Context!");

            } else {
                System.out.println("❌ Token Validation FAILED.");
            }
        } else {
            System.out.println("❌ No valid Bearer token found in header.");
        }

        filterChain.doFilter(request, response);
    }
}
