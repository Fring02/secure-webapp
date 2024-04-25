package com.example.password_encryption.util;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler({InvalidCredentialsException.class, IllegalArgumentException.class, EntityNotFoundException.class,
    EntityExistsException.class})
    public ResponseEntity<String> handleBadRequest(Exception e){
        logger.warn("Bad request: " + e.getMessage());
        if(e.getMessage() == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.badRequest().body(e.getMessage());
    }
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
     HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        StringBuilder sb = new StringBuilder();
        var validationErrors = ex.getBindingResult().getAllErrors();
        validationErrors.forEach(e -> sb.append(e.getDefaultMessage()).append("\n"));
        logger.warn("Bad request, request body validation failure: " + sb);
        var error = validationErrors.get(0);
        return ResponseEntity.badRequest().body(error.getDefaultMessage());
    }
    @ExceptionHandler({InvalidKeySpecException.class, NoSuchAlgorithmException.class})
    public ResponseEntity<String> handleHashingError(Exception e){
        logger.error(e.getMessage());
        return ResponseEntity.status(500).body(e.getMessage());
    }
}