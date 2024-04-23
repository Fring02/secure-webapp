package com.example.password_encryption.data;

import com.example.password_encryption.model.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilesRepository extends JpaRepository<File, Long> {
    boolean existsByName(String name);
}
