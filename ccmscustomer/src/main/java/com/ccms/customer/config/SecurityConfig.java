package com.ccms.customer.config;

import com.ccms.customer.utilities.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration class for the application that configures HTTP security settings.
 * <p>
 * This class uses Spring Security to configure the security filter chain for the application, 
 * including JWT authentication. The configuration disables CSRF protection and allows 
 * unauthenticated access to certain endpoints (such as Swagger UI and authentication endpoints).
 * For all other endpoints, authentication is required.
 * </p>
 * 
 * @since 1.0
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	


    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	
        http.csrf(csrf -> csrf.disable()) // Disable CSRF protection
            .authorizeHttpRequests(requests -> requests
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui/index.html","/actuator/prometheus").permitAll()  // Allow access to Swagger UI
                .requestMatchers("/api/customer/authenticate", "/api/customer/register").permitAll() // Allow unauthenticated access to auth endpoints
                .anyRequest().authenticated()) // Require authentication for all other endpoints
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT filter before the default filter

        return http.build();
    }
}



