package com.ccms.service.utilities;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class CreditCardEnDecryption {
	
	// Inject the AES key from application.properties using @Value
    @Value("${aes.key}")
    private String constantKeyBase64; 

    // Encrypt the credit card number using AES
    public String encrypt(String creditCardNumber) throws Exception {
      
    	 // Convert the fixed key from Base64 to bytes
        byte[] decodedKey = Base64.getDecoder().decode(constantKeyBase64);
        
        SecretKey secretKey = new SecretKeySpec(decodedKey, "AES");
        
        // Generate a random Initialization Vector (IV)
        byte[] iv = new byte[16]; // AES block size is 16 bytes
        new SecureRandom().nextBytes(iv);
        
        // Create a Cipher instance for AES encryption (CBC mode with PKCS5 padding)
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new javax.crypto.spec.IvParameterSpec(iv));  // Initialize the cipher for encryption

        // Convert the credit card number to bytes and encrypt
        byte[] encryptedBytes = cipher.doFinal(creditCardNumber.getBytes());

        // Combine the IV and encrypted data
        byte[] encryptedDataWithIV = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, encryptedDataWithIV, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, encryptedDataWithIV, iv.length, encryptedBytes.length);

        // Return the encrypted data as a Base64 encoded string
        return Base64.getEncoder().encodeToString(encryptedDataWithIV);
        
    }

    // Decrypt the encrypted credit card number using AES
    public String decrypt(String encryptedCreditCard) throws Exception {
       
    	   // Decode the Base64 encoded ciphertext
        byte[] encryptedDataWithIV = Base64.getDecoder().decode(encryptedCreditCard);

        // Extract the IV from the encrypted data
        byte[] iv = new byte[16]; // AES block size is 16 bytes
        System.arraycopy(encryptedDataWithIV, 0, iv, 0, iv.length);

        // The remaining bytes are the encrypted data
        byte[] encryptedData = new byte[encryptedDataWithIV.length - iv.length];
        System.arraycopy(encryptedDataWithIV, iv.length, encryptedData, 0, encryptedData.length);

    	// Convert the fixed key from Base64 to bytes
        byte[] decodedKey = Base64.getDecoder().decode(constantKeyBase64);
        SecretKey secretKey = new SecretKeySpec(decodedKey, "AES");
    	
        // Create a Cipher instance for AES DEcryption (CBC mode with PKCS5 padding)
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new javax.crypto.spec.IvParameterSpec(iv));  // Initialize the cipher for decryption

        // Decrypt the ciphertext
        byte[] decryptedBytes = cipher.doFinal(encryptedData);

        // Return the decrypted data as a string
        return new String(decryptedBytes);
    }

}
