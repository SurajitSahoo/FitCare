package com.fitness.u_service.dto;

import com.fitness.u_service.models.UserRole;
import lombok.Data;

import java.time.LocalDateTime;

@Data

public class UserResponse {
    private String id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
