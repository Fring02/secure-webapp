package com.example.password_encryption.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class TokensBody {
    private String accessToken;
    private String refreshToken;
}