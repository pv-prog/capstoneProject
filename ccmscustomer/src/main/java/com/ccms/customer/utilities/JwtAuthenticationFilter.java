package com.ccms.customer.utilities;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;

/**
 * Filter that intercepts HTTP requests to extract and validate JWT tokens.
 * <p>
 * This filter checks for a JWT token in the "Authorization" header, validates it, and if valid,
 * sets the authentication in the Spring Security context, allowing the user to access protected resources.
 * </p>
 */

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    // Constructor injection of JwtUtil
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Extract token from Authorization header
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
    
            token = token.substring(7); // Remove the "Bearer " prefix

            // Extract username from the token
            String username = jwtUtil.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            	
                
                if (jwtUtil.validateToken(token, username)) {
                    // Create an authentication token with roles/authorities
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            username, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                    );
                    // Set the authentication context
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}


