package com.ccms.customer.model;

/**
 * Represents the response containing a JWT token.
 * <p>
 * This class is used to return the JWT (JSON Web Token) to the client after successful authentication.
 * It encapsulates the token string that can be used by the client to authenticate future requests.
 */

public class JwtResponse {
    
    private String token;

    public JwtResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
