package com.meetwo.service;

import com.meetwo.dto.request.CreateUserRequest;
import com.meetwo.dto.request.UpdateUserRequest;
import com.meetwo.dto.response.UserResponse;
import com.meetwo.enums.Gender;
import com.meetwo.enums.Interest;
import com.meetwo.enums.RelationshipType;

import java.util.List;
import java.util.Set;

public interface UserService {

    // CRUD Operations
    UserResponse createUser(CreateUserRequest request);
    UserResponse getUserById(Long id);
    UserResponse getUserByUsername(String username);
    UserResponse getUserByEmail(String email);
    List<UserResponse> getAllUsers();
    UserResponse updateUser(Long id, UpdateUserRequest request);
    void deleteUser(Long id);

    // Verification methods
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // Search methods for dating features
    List<UserResponse> getUsersByCity(String city);
    List<UserResponse> getUsersByGender(Gender gender);
    List<UserResponse> getUsersByRelationshipType(RelationshipType relationshipType);
    List<UserResponse> getUsersByInterests(Set<Interest> interests);
    List<UserResponse> getUsersByAgeRange(int minAge, int maxAge);

    // Advanced matching methods
    List<UserResponse> findPotentialMatches(Gender gender, RelationshipType relationshipType,
                                            String city, Integer minAge, Integer maxAge);
    List<UserResponse> findUsersWithSimilarInterests(Long userId);
    List<UserResponse> getNewestUsers();
    List<UserResponse> getUsersWithBiography();

    // Statistics methods
    long countUsersByGender(Gender gender);
    long countUsersByCity(String city);

    // Utility methods
    List<UserResponse> getUsersExcluding(List<Long> excludedIds);
}