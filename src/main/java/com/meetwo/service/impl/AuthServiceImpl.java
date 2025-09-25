package com.meetwo.service.impl;

import com.meetwo.dto.mapper.UserMapper;
import com.meetwo.dto.request.CreateUserRequest;
import com.meetwo.dto.request.LoginRequest;
import com.meetwo.dto.response.AuthResponse;
import com.meetwo.dto.response.UserResponse;
import com.meetwo.entity.User;
import com.meetwo.exception.user.UserAlreadyExistsException;
import com.meetwo.exception.user.UserNotFoundException;
import com.meetwo.repository.UserRepository;
import com.meetwo.security.JwtUtil;
import com.meetwo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(CreateUserRequest request) {
        // Vérifications des doublons (comme dans ton UserServiceImpl)
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("username", request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("email", request.getEmail());
        }

        // Création de l'utilisateur (réutilise ton mapper existant)
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        // Génération du token JWT
        String token = jwtUtil.generateToken(savedUser);
        UserResponse userResponse = userMapper.toResponse(savedUser);

        return new AuthResponse(token, userResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        try {
            // Authentification avec Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsernameOrEmail(),
                            request.getPassword()
                    )
            );

            // Récupération de l'utilisateur authentifié
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Récupération de l'utilisateur complet depuis la base
            User user = userRepository.findByUsernameOrEmail(
                    request.getUsernameOrEmail(),
                    request.getUsernameOrEmail()
            ).orElseThrow(() -> new UserNotFoundException("username or email", request.getUsernameOrEmail()));

            // Génération du token
            String token = jwtUtil.generateToken(userDetails);
            UserResponse userResponse = userMapper.toResponse(user);

            return new AuthResponse(token, userResponse);

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Nom d'utilisateur/email ou mot de passe incorrect");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse validateToken(String token) {
        try {
            String username = jwtUtil.extractUsername(token);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException("username", username));

            // Vérifier si le token est encore valide
            if (jwtUtil.validateToken(token, user)) {
                UserResponse userResponse = userMapper.toResponse(user);
                return new AuthResponse(token, userResponse);
            } else {
                throw new BadCredentialsException("Token invalide ou expiré");
            }
        } catch (Exception e) {
            throw new BadCredentialsException("Token invalide");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String token) {
        String username = jwtUtil.extractUsername(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("username", username));

        return userMapper.toResponse(user);
    }
}