package com.example.password_encryption.service;

import com.example.password_encryption.model.TokensBody;
import com.example.password_encryption.security.crypto.RsaKeyGen;
import com.example.password_encryption.security.hashing.PasswordHasher;
import com.example.password_encryption.data.UsersRepository;
import com.example.password_encryption.dto.UserDto;
import com.example.password_encryption.model.User;
import com.example.password_encryption.util.InvalidCredentialsException;
import com.example.password_encryption.util.JwtUtilService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {
    private final UsersRepository repository;
    private final PasswordHasher hashingService;
    private final JwtUtilService jwtService;
    @Value("${jwt.expiration.refresh}")
    private int expirationForRefresh;
    public UserService(UsersRepository repository, PasswordHasher hashingService, JwtUtilService jwtUtilService) {
        this.repository = repository;
        this.hashingService = hashingService;
        this.jwtService = jwtUtilService;
    }
    public TokensBody register(UserDto userDto) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidCredentialsException, IOException {
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

        var refreshToken = jwtService.generateRefreshToken();
        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiryDate(LocalDate.now().plusDays(expirationForRefresh));
        user = repository.save(user);
        log.info("Added user with id {} to database;", user.getId());

        RsaKeyGen keyGenerator = new RsaKeyGen();
        PublicKey publicKey = keyGenerator.getPublicKey();
        keyGenerator.saveOrRewritePrivateKey(user.getId());
        var claims = new HashMap<String, Object>();
        claims.put("public_key", Hex.encodeHexString(publicKey.getEncoded()));
        final String accessToken = jwtService.generateAccessToken(user, claims);

        return new TokensBody(accessToken, refreshToken);
    }
    public TokensBody login(UserDto userDto) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidCredentialsException, IOException {
        Optional<User> userOpt = repository.findByUsername(userDto.getUsername());
        if(userOpt.isEmpty()) throw new InvalidCredentialsException("Username not found");
        User user = userOpt.get();
        if(!hashingService.verify(userDto.getPassword(), user.getHash(), user.getSalt()))
            throw new InvalidCredentialsException("Wrong username or password");
        log.info("Successful login for user {}", user.getId());

        var newRefreshToken = jwtService.generateRefreshToken();
        user.setRefreshToken(newRefreshToken);
        user.setRefreshTokenExpiryDate(LocalDate.now().plusDays(expirationForRefresh));
        repository.save(user);

        RsaKeyGen keyGenerator = new RsaKeyGen();
        PublicKey publicKey = keyGenerator.getPublicKey();
        keyGenerator.saveOrRewritePrivateKey(user.getId());

        var claims = new HashMap<String, Object>();
        claims.put("public_key", Hex.encodeHexString(publicKey.getEncoded()));

        final String newAccessToken = jwtService.generateAccessToken(user, claims);
        return new TokensBody(newAccessToken, newRefreshToken);
    }
    public List<User> getAllUsers(){
        return repository.findAll();
    }
}
