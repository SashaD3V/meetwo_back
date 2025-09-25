package com.meetwo.service;

import com.meetwo.dto.request.CreateUserRequest;
import com.meetwo.dto.request.LoginRequest;
import com.meetwo.dto.response.AuthResponse;
import com.meetwo.dto.response.UserResponse;

public interface AuthService {

    // Inscription d'un nouvel utilisateur
    AuthResponse register(CreateUserRequest request);

    // Connexion utilisateur
    AuthResponse login(LoginRequest request);

    // Validation d'un token JWT
    AuthResponse validateToken(String token);

    // Récupération de l'utilisateur actuel depuis un token
    UserResponse getCurrentUser(String token);
}