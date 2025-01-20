package com.ccms.service.exception;

public class CreditCardNotFoundException extends RuntimeException{
	    public CreditCardNotFoundException(String username) {
	        super("No credit card found for username: " + username);
	    }
	}