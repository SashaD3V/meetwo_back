package com.meetwo.dto.request;

import com.meetwo.enums.Gender;
import com.meetwo.enums.Interest;
import com.meetwo.enums.RelationshipType;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Data
public class CreateUserRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private String firstName;
    private String lastName;
    private LocalDate birthDate;

    @NotNull(message = "Gender is required")
    private Gender gender;

    private String biography;
    private String city;
    private Set<Interest> interests;

    @NotNull(message = "Seeking relationship type is required")
    private RelationshipType seekingRelationshipType;
}