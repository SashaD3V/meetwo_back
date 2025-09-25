package com.meetwo.dto.response;

import com.meetwo.enums.Gender;
import com.meetwo.enums.Interest;
import com.meetwo.enums.RelationshipType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@Schema(description = "Réponse contenant les informations d'un utilisateur")
public class UserResponse {

    @Schema(description = "Identifiant unique", example = "1")
    private Long id;

    @Schema(description = "Nom d'utilisateur", example = "john_doe")
    private String username;

    @Schema(description = "Adresse email", example = "john@example.com")
    private String email;

    @Schema(description = "Nom d'affichage", example = "John Doe")
    private String name;

    @Schema(description = "Prénom", example = "John")
    private String firstName;

    @Schema(description = "Nom de famille", example = "Doe")
    private String lastName;

    @Schema(description = "Date de naissance", example = "1995-09-21")
    private LocalDate birthDate;

    @Schema(description = "Âge calculé", example = "28")
    private Integer age;

    @Schema(description = "Genre", example = "HOMME")
    private Gender gender;

    @Schema(description = "Biographie", example = "Passionné de sport et de voyage")
    private String biography;

    @Schema(description = "Ville", example = "Paris")
    private String city;

    @Schema(description = "Centres d'intérêt")
    private Set<Interest> interests;

    @Schema(description = "Type de relation recherchée", example = "RELATION_SERIEUSE")
    private RelationshipType seekingRelationshipType;
}