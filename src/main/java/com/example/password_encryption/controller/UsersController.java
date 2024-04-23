package com.example.password_encryption.controller;

import com.example.password_encryption.dto.UserDto;
import com.example.password_encryption.model.TokensBody;
import com.example.password_encryption.service.UserService;
import com.example.password_encryption.util.InvalidCredentialsException;
import com.example.password_encryption.util.JwtUtilService;
import io.jsonwebtoken.JwtException;
import io.micrometer.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@RestController
@RequestMapping("/api/users")
public class UsersController {
    private final UserService service;
    private final JwtUtilService jwtService;
    private final Logger logger = LoggerFactory.getLogger(UsersController.class);
    public UsersController(UserService service, JwtUtilService jwtService) {
        this.service = service;
        this.jwtService = jwtService;
    }
    @GetMapping("/hello")
    public String getHello(){
        return "hello";
    }
    @PostMapping("/register")
    public ResponseEntity<TokensBody> registerUser(@RequestBody UserDto dto) throws InvalidCredentialsException, NoSuchAlgorithmException, InvalidKeySpecException {
        var tokens = service.register(dto);
        return ResponseEntity.ok(tokens);
    }
    @PostMapping("/login")
    public ResponseEntity<TokensBody> loginUser(@RequestBody UserDto dto) throws InvalidCredentialsException, NoSuchAlgorithmException, InvalidKeySpecException {
        var tokens = service.login(dto);
        return ResponseEntity.ok(tokens);
    }
    @GetMapping
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String token){
        token = token.substring(7);
        if(StringUtils.isBlank(token)) return ResponseEntity.status(401).body("Unauthorized");
        try {
            if(!jwtService.isValid(token)) {
                logger.warn("Couldn't fetch user from token, unauthorized");
                return ResponseEntity.status(401).body("Unauthorized");
            }
            return ResponseEntity.ok(service.getAllUsers());
        }
        catch (IllegalArgumentException | JwtException e){
            logger.warn("Unauthorized: " + e.getMessage());
            return ResponseEntity.status(401).body("Unauthorized");
        }
    }
}
