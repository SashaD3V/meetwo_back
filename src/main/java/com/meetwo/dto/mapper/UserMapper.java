package com.meetwo.dto.mapper;

import com.meetwo.dto.request.CreateUserRequest;
import com.meetwo.dto.request.UpdateUserRequest;
import com.meetwo.dto.response.UserResponse;
import com.meetwo.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        if (user == null) return null;

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setName(user.getName()); // AJOUT du champ name
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setBirthDate(user.getBirthDate());
        response.setAge(user.getAge());
        response.setGender(user.getGender());
        response.setBiography(user.getBiography());
        response.setCity(user.getCity());
        response.setInterests(user.getInterests());
        response.setSeekingRelationshipType(user.getSeekingRelationshipType());
        return response;
    }

    public User toEntity(CreateUserRequest request) {
        if (request == null) return null;

        User user = new User();
        user.setUsername(request.getUsername()); // MAINTENANT ça marche
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setBirthDate(request.getBirthDate());
        user.setGender(request.getGender());
        user.setBiography(request.getBiography());
        user.setCity(request.getCity());
        user.setInterests(request.getInterests());
        user.setSeekingRelationshipType(request.getSeekingRelationshipType());

        // Génération automatique du name
        user.generateName();

        return user;
    }

    public void updateEntity(User user, UpdateUserRequest request) {
        if (request == null || user == null) return;

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getBirthDate() != null) {
            user.setBirthDate(request.getBirthDate());
        }
        if (request.getBiography() != null) {
            user.setBiography(request.getBiography());
        }
        if (request.getCity() != null) {
            user.setCity(request.getCity());
        }
        if (request.getInterests() != null) {
            user.setInterests(request.getInterests());
        }

        // Régénération du name après modification
        user.generateName();
    }
}