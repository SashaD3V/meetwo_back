package com.meetwo.controller;

import com.meetwo.dto.mapper.UserMapper;
import com.meetwo.dto.request.CreateUserRequest;
import com.meetwo.dto.request.LoginRequest;
import com.meetwo.dto.response.AuthResponse;
import com.meetwo.dto.response.UserResponse;
import com.meetwo.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API d'authentification JWT")
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    @Operation(summary = "Inscription", description = "Créer un compte et recevoir un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "409", description = "Utilisateur déjà existant")
    })
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody CreateUserRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Connexion", description = "Se connecter et recevoir un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Connexion réussie",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Identifiants invalides")
    })
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Profil utilisateur", description = "Récupérer le profil de l'utilisateur connecté")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profil récupéré",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token manquant ou invalide")
    })
    public ResponseEntity<UserResponse> getCurrentUser(
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.substring(7); // Retirer "Bearer "
        UserResponse response = authService.getCurrentUser(token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate")
    @Operation(summary = "Valider token", description = "Vérifier si un token JWT est encore valide")
    public ResponseEntity<AuthResponse> validateToken(
            @RequestParam String token) {
        AuthResponse response = authService.validateToken(token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Déconnexion", description = "Information sur la déconnexion côté client")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Déconnexion réussie. Supprimez le token côté client.");
    }
}