package com.meetwo.controller;

import com.meetwo.dto.request.CreateUserRequest;
import com.meetwo.dto.request.UpdateUserRequest;
import com.meetwo.dto.response.UserResponse;
import com.meetwo.enums.Gender;
import com.meetwo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "API de gestion des utilisateurs de l'application de rencontre")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(
            summary = "Créer un nouvel utilisateur",
            description = "Crée un nouveau compte utilisateur pour l'application de rencontre"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Utilisateur créé avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Données d'entrée invalides (email/username déjà existant, champs manquants)"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflit - utilisateur déjà existant"
            )
    })
    public ResponseEntity<UserResponse> createUser(
            @Parameter(description = "Données pour créer un nouvel utilisateur")
            @Valid @RequestBody CreateUserRequest request) {
        UserResponse user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Récupérer un utilisateur par ID",
            description = "Retourne les détails d'un utilisateur spécifique basé sur son identifiant"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Utilisateur trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Utilisateur non trouvé"
            )
    })
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "ID de l'utilisateur à récupérer", example = "1")
            @PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/username/{username}")
    @Operation(
            summary = "Récupérer un utilisateur par nom d'utilisateur",
            description = "Retourne les détails d'un utilisateur basé sur son nom d'utilisateur"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Utilisateur trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Utilisateur non trouvé"
            )
    })
    public ResponseEntity<UserResponse> getUserByUsername(
            @Parameter(description = "Nom d'utilisateur", example = "john_doe")
            @PathVariable String username) {
        UserResponse user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    @Operation(
            summary = "Récupérer tous les utilisateurs",
            description = "Retourne la liste complète de tous les utilisateurs inscrits"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste des utilisateurs récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class)
                    )
            )
    })
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Mettre à jour un utilisateur",
            description = "Met à jour les informations d'un utilisateur existant"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Utilisateur mis à jour avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Utilisateur non trouvé"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Données d'entrée invalides"
            )
    })
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "ID de l'utilisateur à mettre à jour", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nouvelles données de l'utilisateur")
            @Valid @RequestBody UpdateUserRequest request) {
        UserResponse user = userService.updateUser(id, request);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Supprimer un utilisateur",
            description = "Supprime définitivement un utilisateur du système"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Utilisateur supprimé avec succès"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Utilisateur non trouvé"
            )
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID de l'utilisateur à supprimer", example = "1")
            @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/city/{city}")
    @Operation(
            summary = "Rechercher des utilisateurs par ville",
            description = "Retourne tous les utilisateurs habitant dans une ville spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste des utilisateurs de la ville récupérée",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class)
                    )
            )
    })
    public ResponseEntity<List<UserResponse>> getUsersByCity(
            @Parameter(description = "Nom de la ville", example = "Paris")
            @PathVariable String city) {
        List<UserResponse> users = userService.getUsersByCity(city);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search/gender/{gender}")
    @Operation(
            summary = "Rechercher des utilisateurs par genre",
            description = "Retourne tous les utilisateurs d'un genre spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste des utilisateurs du genre spécifié",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Genre invalide"
            )
    })
    public ResponseEntity<List<UserResponse>> getUsersByGender(
            @Parameter(description = "Genre de l'utilisateur", example = "MALE",
                    schema = @Schema(implementation = Gender.class))
            @PathVariable Gender gender) {
        List<UserResponse> users = userService.getUsersByGender(gender);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search/age-range")
    @Operation(
            summary = "Rechercher des utilisateurs par tranche d'âge",
            description = "Retourne tous les utilisateurs dans une tranche d'âge spécifiée"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste des utilisateurs dans la tranche d'âge",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Paramètres d'âge invalides"
            )
    })
    public ResponseEntity<List<UserResponse>> getUsersByAgeRange(
            @Parameter(description = "Âge minimum", example = "25")
            @RequestParam int minAge,
            @Parameter(description = "Âge maximum", example = "35")
            @RequestParam int maxAge) {
        List<UserResponse> users = userService.getUsersByAgeRange(minAge, maxAge);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/check/username/{username}")
    @Operation(
            summary = "Vérifier la disponibilité d'un nom d'utilisateur",
            description = "Vérifie si un nom d'utilisateur est déjà pris"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Vérification effectuée",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class, example = "true")
                    )
            )
    })
    public ResponseEntity<Boolean> checkUsernameExists(
            @Parameter(description = "Nom d'utilisateur à vérifier", example = "john_doe")
            @PathVariable String username) {
        boolean exists = userService.existsByUsername(username);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/check/email/{email}")
    @Operation(
            summary = "Vérifier la disponibilité d'un email",
            description = "Vérifie si une adresse email est déjà utilisée"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Vérification effectuée",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class, example = "false")
                    )
            )
    })
    public ResponseEntity<Boolean> checkEmailExists(
            @Parameter(description = "Adresse email à vérifier", example = "john@example.com")
            @PathVariable String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }
}