package com.fitness.u_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invaild email format")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password should have 6 charecter")
    private String password;
    private String firstName;
    private String lastName;
    private String keycloakId;
}
