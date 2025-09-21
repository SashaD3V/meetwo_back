package com.meetwo.dto.request;

import com.meetwo.enums.Interest;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String biography;
    private String city;
    private Set<Interest> interests;
}