package com.ccms.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ccms.service.utilities.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidUsernameException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUsernameException(InvalidUsernameException ex) {
        // Log the error or any additional processing
        // Return a custom error response
        ErrorResponse errorResponse = new ErrorResponse("Invalid username", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    
    @ExceptionHandler(DuplicateCreditCardException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateCreditCardException(DuplicateCreditCardException ex) {
        // Log the error or any additional processing
        // Return a custom error response
        ErrorResponse errorResponse = new ErrorResponse("Credit card already associated with this user", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
