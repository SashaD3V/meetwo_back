package com.meetwo.dto.response;

import com.meetwo.enums.Gender;
import com.meetwo.enums.Interest;
import com.meetwo.enums.RelationshipType;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String name; // IMPORTANT : ce champ doit exister
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Integer age;
    private Gender gender;
    private String biography;
    private String city;
    private Set<Interest> interests;
    private RelationshipType seekingRelationshipType;
}