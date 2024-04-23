package com.example.password_encryption.controller;

import com.example.password_encryption.dto.UserDto;
import com.example.password_encryption.service.UserService;
import com.example.password_encryption.util.InvalidCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@RestController
@RequestMapping("/api/users/")
public class UsersController {
    private final UserService service;
    public UsersController(UserService service) {
        this.service = service;
    }
    @GetMapping("/hello")
    public String getHello(){
        return "hello";
    }
    @PostMapping("register")
    public String registerUser(@RequestBody UserDto dto) throws InvalidCredentialsException, NoSuchAlgorithmException, InvalidKeySpecException {
        service.register(dto);
        return "New user added";
    }
    @PostMapping("login")
    public String loginUser(@RequestBody UserDto dto) throws InvalidCredentialsException, NoSuchAlgorithmException, InvalidKeySpecException {
        service.login(dto);
        return "User logged in";
    }
}
