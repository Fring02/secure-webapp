package com.example.password_encryption.dto;

import java.time.LocalDateTime;

public interface FileDto {
    String getName();
    long getId();
    LocalDateTime getUploadDate();
}
