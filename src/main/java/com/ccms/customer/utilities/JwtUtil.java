package com.ccms.customer.utilities;
import io.jsonwebtoken.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


//JWT Utility for Token Generation and Validation
@Configuration
public class JwtUtil {
	
    
	
	@Value("${jwt.secret}")
	private String secretKey; // Your secret key for signing/verifying JWT
    
    public String generateToken(String username) {
    	
    	System.out.println("Generate Token!!");
    	
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                   .setClaims(claims)                // Add custom claims (e.g., roles, permissions)
                   .setSubject(username)             // Set the subject to the username (the user the token represents)
                   .setIssuedAt(new Date())          // Set the issued time to the current time 
                   .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // Set expiration to 1 hours from now
                   .signWith(SignatureAlgorithm.HS256, secretKey)     // Sign the JWT with HS256 and the secret key
                   .compact();                // Return the JWT as a compact string (<base64UrlEncodedHeader>.<base64UrlEncodedPayload>.<base64UrlEncodedSignature>)
    }

    public String extractUsername(String token) {
    	
    	System.out.println("extractUsername Token!!");
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
      	System.out.println("extractExpiration Token!!");
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, ClaimsResolver<T> claimsResolver) {
    	System.out.println("extractClaim Token!!");
        final Claims claims = extractAllClaims(token);    // Extract claims from the JWT
        return claimsResolver.resolve(claims);            // Apply the claim resolver (like getSubject)
    }

    
    // Extract the Claims (payload) from the JWT
    private Claims extractAllClaims(String token) {
    	System.out.println("extractAllClaims Token!!");
        return Jwts.parser()
                   .setSigningKey(secretKey)    // Use the same secret key to verify the signature
                   .parseClaimsJws(token)       // Parse the token to get the Claims
                   .getBody();                  // Extract the Claims (payload)
    }

    private Boolean isTokenExpired(String token) {
    	System.out.println("isTokenExpired Token!!");
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, String username) {
    	System.out.println("validateToken Token!!");
        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
    }

    @FunctionalInterface
    public interface ClaimsResolver<T> {
        T resolve(Claims claims);
    }
}
