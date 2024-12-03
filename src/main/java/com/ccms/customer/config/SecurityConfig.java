package com.ccms.customer.config;

import com.ccms.customer.utilities.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	


    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	System.out.println("SecurityFilterChain Token!!");
        http.csrf(csrf -> csrf.disable()) // Disable CSRF protection
            .authorizeHttpRequests(requests -> requests
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui/index.html").permitAll()  // Allow access to Swagger UI
                .requestMatchers("/api/customer/authenticate", "/api/customer/register").permitAll() // Allow unauthenticated access to auth endpoints
                .anyRequest().authenticated()) // Require authentication for all other endpoints
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT filter before the default filter

     	System.out.println("SecurityFilterChain END Token!!");
        return http.build();
    }
}



