package com.ccms.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the authentication request sent by the client to authenticate a customer.
 * <p>
 * This class holds the username and password of the customer for authentication purposes.
 * It is used by the authentication endpoint to validate the credentials and issue a JWT token.
 */

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public class AuthenticationRequest {
	
	    private String username;
	    private String password;
	
	}
