package com.example.password_encryption.dto;

import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

public interface FileDto {
    String getName();
    long getId();
    LocalDateTime getUploadDate();
    @Value("#{target.content.length/1024}")
    double getSize();
}
