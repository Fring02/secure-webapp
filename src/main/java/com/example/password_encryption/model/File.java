package com.example.password_encryption.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "files")
@Getter
@Setter
@NoArgsConstructor
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id", nullable = false)
    private long id;
    @Column(name = "file_name", nullable = false, unique = true)
    private String name;
    @Column(name = "user_id", nullable = false)
    private long userId;
    @Column(name = "content", nullable = false)
    private byte[] content;
    public File(String name, long userId, byte[] content) {
        this.name = name;
        this.userId = userId;
        this.content = content;
    }
}
