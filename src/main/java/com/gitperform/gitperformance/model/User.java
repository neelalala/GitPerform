package com.gitperform.gitperformance.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Setter
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;
    private String displayName;
    private String profilePhoto;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // constructors, getters, setters
}