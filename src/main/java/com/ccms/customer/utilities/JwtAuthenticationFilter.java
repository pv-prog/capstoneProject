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

     	System.out.println("doFilterInternal Token!!");
        
     	System.out.println("doFilterInternal#### Token!!"+token);
     	
        if (token != null && token.startsWith("Bearer ")) {
         	System.out.println("doFilterInternal Token!!"+token);
            token = token.substring(7); // Remove the "Bearer " prefix

            // Extract username from the token
            String username = jwtUtil.extractUsername(token);

           	System.out.println("doFilterInternal username!!"+username);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            	
            	System.out.println("username!!!!"+username);
            	System.out.println("Authenticated User: " + SecurityContextHolder.getContext().getAuthentication());
                
                if (jwtUtil.validateToken(token, username)) {
                    // Create an authentication token with roles/authorities
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            username, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                    );
                    // Set the authentication context
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("Authenticated User!!!@@@: " + SecurityContextHolder.getContext().getAuthentication());
                }
            }
        }

        // Continue the filter chain
        
     	System.out.println("filterChain username!!");
        filterChain.doFilter(request, response);
    }
}


