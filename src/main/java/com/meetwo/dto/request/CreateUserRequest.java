package com.meetwo.dto.request;

import com.meetwo.enums.Gender;
import com.meetwo.enums.Interest;
import com.meetwo.enums.RelationshipType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Data
@Schema(description = "Données requises pour créer un nouvel utilisateur")
public class CreateUserRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Schema(description = "Nom d'utilisateur unique", example = "john_doe")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Schema(description = "Adresse email valide", example = "john@example.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Schema(description = "Mot de passe (minimum 6 caractères)", example = "password123")
    private String password;

    @Schema(description = "Prénom", example = "John")
    private String firstName;

    @Schema(description = "Nom de famille", example = "Doe")
    private String lastName;

    @Schema(description = "Date de naissance", example = "1995-09-21")
    private LocalDate birthDate;

    @NotNull(message = "Gender is required")
    @Schema(description = "Genre", example = "HOMME", allowableValues = {"HOMME", "FEMME"})
    private Gender gender;

    @Schema(description = "Biographie", example = "Passionné de sport et de voyage")
    private String biography;

    @Schema(description = "Ville", example = "Paris")
    private String city;

    @Schema(description = "Centres d'intérêt",
            example = "[\"SPORT\", \"MUSIQUE\"]",
            allowableValues = {"SPORT", "MUSIQUE", "CINEMA", "VOYAGE", "CUISINE", "LECTURE", "ART", "JEUX_VIDEO", "FITNESS", "NATURE", "TECHNOLOGIE", "PHOTOGRAPHIE", "DANSE", "MODE", "THEATRE", "RANDONNEE", "YOGA", "MEDITATION", "ANIMAUX", "JARDINAGE", "BRICOLAGE", "SHOPPING", "SORTIES_NOCTURNES", "CONCERTS", "FESTIVALS"})
    private Set<Interest> interests;

    @NotNull(message = "Seeking relationship type is required")
    @Schema(description = "Type de relation recherchée",
            example = "RELATION_SERIEUSE",
            allowableValues = {"RELATION_SERIEUSE", "RELATION_CASUAL"})
    private RelationshipType seekingRelationshipType;
}