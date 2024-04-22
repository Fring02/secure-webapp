package com.example.password_encryption.service;

import com.example.password_encryption.crypto.hashing.PasswordHasher;
import com.example.password_encryption.data.UsersRepository;
import com.example.password_encryption.dto.UserDto;
import com.example.password_encryption.model.User;
import com.example.password_encryption.util.InvalidCredentialsException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;

@Service
@Slf4j
public class UserService {
    private final UsersRepository repository;
    private final PasswordHasher hashingService;
    public UserService(UsersRepository repository, PasswordHasher hashingService) {
        this.repository = repository;
        this.hashingService = hashingService;
    }
    public void register(UserDto userDto) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidCredentialsException {
        var user = new User();
        user.setUsername(userDto.getUsername());
        if(repository.existsByUsername(userDto.getUsername()))
            throw new InvalidCredentialsException("Username already exists");
        byte[] salt = hashingService.generateSalt();
        log.info("Generated salt {} for new user;", hashingService.toHex(salt));
        user.setSalt(salt);
        byte[] passwordHash = hashingService.generateHash(userDto.getPassword(), salt);
        log.info("Generated hash {} for new user;", hashingService.toHex(passwordHash));
        user.setHash(passwordHash);
        user = repository.save(user);
        log.info("Added user with id {} to database;", user.getId());
    }
    public void login(UserDto userDto) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidCredentialsException {
        if(!repository.existsByUsername(userDto.getUsername())) throw new InvalidCredentialsException("Username not found");
        User user = repository.findByUsername(userDto.getUsername());
        if(!hashingService.verify(userDto.getPassword(), user.getHash(), user.getSalt()))
            throw new InvalidCredentialsException("Wrong username or password");
        log.info("Successful login for user {}", user.getId());
    }
}
