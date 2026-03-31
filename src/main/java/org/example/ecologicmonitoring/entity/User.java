package org.example.ecologicmonitoring.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String city;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private boolean emailVerified = false;

    @Column
    private String verificationCode;

    private LocalDateTime createdAt = LocalDateTime.now();

    // User.java ichiga
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<UserCity> cities = new ArrayList<>();

    public enum Role {
        USER, ADMIN
    }
}