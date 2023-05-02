package com.example.ratelimiter.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(name = "\"user\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column(name = "id", updatable = false)
        private Integer id;
        @Size(min = 3, message = "names should have more than three characters")
        @Column(name = "names", columnDefinition = "TEXT", nullable = false

        )
        private String names;
        @NotNull
        @Email
        @Column(name = "email", nullable = false, columnDefinition = "TEXT", unique = true)
        private String email;
        @NotBlank
        @Size(min = 3, message = "names should have more than three characters")
        @Column(name = "userName", nullable = false, columnDefinition = "TEXT", unique = true)
        private String userName;
        @NotNull
        @NotBlank
        @Size(min = 8, message = "Password must be at least 8 characters long")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "Password must contain at least one lowercase letter, one uppercase letter, and one digit")
        private String password;
        @NotNull
        private boolean status = true;
}
