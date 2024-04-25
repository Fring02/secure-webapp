package com.example.password_encryption.controller;

import com.example.password_encryption.dto.LoginDto;
import com.example.password_encryption.dto.UserDto;
import com.example.password_encryption.model.TokensBody;
import com.example.password_encryption.service.UserService;
import com.example.password_encryption.util.InvalidCredentialsException;
import com.example.password_encryption.util.JwtUtilService;
import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@RestController
@RequestMapping("/api/users")
public class UsersController extends BaseController {
    private final UserService service;
    public UsersController(UserService service, JwtUtilService jwtService) {
        super(LoggerFactory.getLogger(UsersController.class), jwtService);
        this.service = service;
    }
    @PostMapping("/register")
    public ResponseEntity<TokensBody> registerUser(@RequestBody @Valid UserDto dto) throws InvalidCredentialsException, NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        var tokens = service.register(dto);
        return ResponseEntity.ok(tokens);
    }
    @PostMapping("/login")
    public ResponseEntity<TokensBody> loginUser(@RequestBody @Valid LoginDto dto) throws InvalidCredentialsException, NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        var tokens = service.login(dto);
        return ResponseEntity.ok(tokens);
    }
    @GetMapping
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String token){
        validateAndFetchToken(token);
        return ResponseEntity.ok(service.getAllUsers());
    }
}
