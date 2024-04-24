package com.example.password_encryption.util;

import com.example.password_encryption.data.UsersRepository;
import com.example.password_encryption.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.Serial;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtUtilService implements Serializable {
    private final UsersRepository repository;
    @Serial
    private static final long serialVersionUID = -2550185165626007488L;
    @Value("${jwt.expiration.access}")
    private long expirationForAccess;
    @Value("${jwt.secret}")
    private String secretKeyString;
    public JwtUtilService(UsersRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }
    //for retrieving any information from token we will need the secret key
    public Claims getAllClaimsFromToken(String token) {
        SecretKey secret = Keys.hmacShaKeyFor(secretKeyString.getBytes());
        return Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token).getBody();
    }
    //retrieve username from jwt token
    public long getUserIdFromToken(String token) {
        var claims = getAllClaimsFromToken(token);
        return claims.get("id", Long.class);
    }
    //retrieve expiration date from jwt token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    //check if the token has expired
    private boolean isExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    //generate token for user
    public String generateAccessToken(User userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userDetails.getId());
        return generateAccessToken(claims, userDetails.getUsername());
    }
    public String generateRefreshToken(){
        var bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    private String generateAccessToken(Map<String, Object> claims, String subject) {
        SecretKey secret = Keys.hmacShaKeyFor(secretKeyString.getBytes());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationForAccess * 1000))
                .signWith(secret, SignatureAlgorithm.HS256)
                .compact();
    }
    //validate token
    public boolean isValid(String token) {
        if(isExpired(token)) throw new JwtException("Given token is expired");
        long userId = getUserIdFromToken(token);
        return userId > 0;
    }
}