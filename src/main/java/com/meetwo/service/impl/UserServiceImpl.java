package com.meetwo.service.impl;

import com.meetwo.dto.mapper.UserMapper;
import com.meetwo.dto.request.CreateUserRequest;
import com.meetwo.dto.request.UpdateUserRequest;
import com.meetwo.dto.response.UserResponse;
import com.meetwo.entity.User;
import com.meetwo.enums.Gender;
import com.meetwo.enums.Interest;
import com.meetwo.enums.RelationshipType;
import com.meetwo.exception.user.UserAlreadyExistsException;
import com.meetwo.exception.user.UserNotFoundException;
import com.meetwo.repository.UserRepository;
import com.meetwo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("username", request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("email", request.getEmail());
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("username", username));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("email", email));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        userMapper.updateEntity(user, request);
        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        System.out.println("=== DIAGNOSTIC REPOSITORY ===");

        // Test 1: Compter tous les users
        long totalUsers = userRepository.count();
        System.out.println("Total users en base: " + totalUsers);

        // Test 2: Récupérer tous les usernames
        List<User> allUsers = userRepository.findAll();
        System.out.println("Usernames en base:");
        allUsers.forEach(u -> System.out.println(" - '" + u.getUsername() + "'"));

        // Test 3: Test avec findByUsername
        Optional<User> userFound = userRepository.findByUsername(username);
        System.out.println("findByUsername('" + username + "') trouve: " + userFound.isPresent());

        // Test 4: Le existsByUsername original
        boolean exists = userRepository.existsByUsername(username);
        System.out.println("existsByUsername('" + username + "') retourne: " + exists);

        return exists;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByCity(String city) {
        return userRepository.findByCityIgnoreCaseAndEnabledTrue(city).stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByGender(Gender gender) {
        return userRepository.findByGenderAndEnabledTrue(gender).stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByRelationshipType(RelationshipType relationshipType) {
        return userRepository.findBySeekingRelationshipTypeAndEnabledTrue(relationshipType).stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByInterests(Set<Interest> interests) {
        return userRepository.findByInterestsIn(interests).stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByAgeRange(int minAge, int maxAge) {
        return userRepository.findByAgeBetween(minAge, maxAge).stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findPotentialMatches(Gender gender, RelationshipType relationshipType,
                                                   String city, Integer minAge, Integer maxAge) {
        return userRepository.findPotentialMatches(gender, relationshipType, city, minAge, maxAge).stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findUsersWithSimilarInterests(Long userId) {
        return userRepository.findUsersWithSimilarInterests(userId).stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getNewestUsers() {
        return userRepository.findNewestUsers().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersWithBiography() {
        return userRepository.findByBiographyIsNotNullAndEnabledTrueOrderByUpdatedAtDesc().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countUsersByGender(Gender gender) {
        return userRepository.countByGenderAndEnabledTrue(gender);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUsersByCity(String city) {
        return userRepository.countByCityIgnoreCaseAndEnabledTrue(city);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersExcluding(List<Long> excludedIds) {
        return userRepository.findUsersExcluding(excludedIds).stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }
}