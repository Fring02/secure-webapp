package com.example.password_encryption.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private long id;
    @Column(name = "username", unique = true, nullable = false)
    private String username;
    @Column(name = "hash", nullable = false)
    private byte[] hash;
    @Column(name = "salt", nullable = false, unique = true)
    private byte[] salt;
    @Column(name = "refreshToken")
    private String refreshToken;
    @Column(name = "refreshTokenExpiryDate")
    private LocalDate refreshTokenExpiryDate = LocalDate.now();
}
