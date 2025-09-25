package com.meetwo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Données de connexion")
public class LoginRequest {

    @NotBlank(message = "Username or email is required")
    @Schema(description = "Nom d'utilisateur ou email", example = "john_doe")
    private String usernameOrEmail;

    @NotBlank(message = "Password is required")
    @Schema(description = "Mot de passe", example = "password123")
    private String password;
}