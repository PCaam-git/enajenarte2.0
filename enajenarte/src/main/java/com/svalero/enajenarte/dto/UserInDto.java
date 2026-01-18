package com.svalero.enajenarte.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.*;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInDto {

    @NotEmpty(message = "username is mandatory")
    private String username;

    @NotEmpty(message = "password is mandatory")
    private String password;

    @NotEmpty(message = "email is mandatory")
    @Email(message = "email must be valid")
    private String email;

    @NotEmpty(message = "fullName is mandatory")
    private String fullName;

    private LocalDate registrationDate;

    @Min(value = 12, message = "age must be at least 12")
    @Max(value = 120, message = "age must be realistic")
    private int age;

    private boolean active;

    @Min(value = 0, message = "balance must be a positive number")
    private float balance;

    @NotEmpty(message = "role is mandatory")
    @Pattern(regexp = "^(user|admin)$", message = "role must be 'user' or 'admin'")
    private String role;
}
