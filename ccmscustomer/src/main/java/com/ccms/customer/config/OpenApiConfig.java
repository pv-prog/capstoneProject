package com.ccms.customer.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Configuration class for setting up OpenAPI (Swagger) documentation with custom security configurations.
 * <p>
 * This configuration class defines the OpenAPI specifications, including security schemes required 
 * for API authentication. It specifically configures Bearer Token authentication using JWT (JSON Web Tokens).
 * </p>
 * 
 * @since 1.0
 */

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.components(new Components().addSecuritySchemes("bearerAuth",
						new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
				.security(List.of(new SecurityRequirement().addList("bearerAuth")));
	}
}	