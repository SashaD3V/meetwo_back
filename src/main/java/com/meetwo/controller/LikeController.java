package com.meetwo.controller;

import com.meetwo.dto.request.CreateLikeRequest;
import com.meetwo.dto.request.UpdateLikeRequest;
import com.meetwo.dto.response.LikeResponse;
import com.meetwo.dto.response.MatchResponse;
import com.meetwo.dto.response.LikeStatsResponse;
import com.meetwo.service.LikeService;
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
@RequestMapping("/api/likes")
@RequiredArgsConstructor
@Tag(name = "Likes", description = "API de gestion des likes et matches dans l'application de rencontre")
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    @Operation(summary = "Créer un like", description = "Permet à un utilisateur de liker un autre utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Like créé avec succès",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LikeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides ou utilisateur ne peut se liker lui-même"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
            @ApiResponse(responseCode = "409", description = "Like déjà existant")
    })
    public ResponseEntity<LikeResponse> createLike(@Valid @RequestBody CreateLikeRequest request) {
        LikeResponse like = likeService.createLike(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(like);
    }

    @PostMapping("/quick")
    @Operation(summary = "Like rapide", description = "Permet de liker rapidement avec les IDs en paramètres")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Like créé avec succès",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LikeResponse.class))),
            @ApiResponse(responseCode = "400", description = "IDs invalides"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
            @ApiResponse(responseCode = "409", description = "Like déjà existant")
    })
    public ResponseEntity<LikeResponse> likeUser(
            @Parameter(description = "ID de l'utilisateur qui like", required = true)
            @RequestParam Long likerId,
            @Parameter(description = "ID de l'utilisateur liké", required = true)
            @RequestParam Long likedUserId) {
        LikeResponse like = likeService.likeUser(likerId, likedUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(like);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un like", description = "Met à jour les informations d'un like")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Like mis à jour avec succès",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LikeResponse.class))),
            @ApiResponse(responseCode = "404", description = "Like non trouvé"),
            @ApiResponse(responseCode = "400", description = "Données d'entrée invalides")
    })
    public ResponseEntity<LikeResponse> updateLike(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLikeRequest request) {
        LikeResponse like = likeService.updateLike(id, request);
        return ResponseEntity.ok(like);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un like", description = "Récupère un like par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Like trouvé",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LikeResponse.class))),
            @ApiResponse(responseCode = "404", description = "Like non trouvé")
    })
    public ResponseEntity<LikeResponse> getLikeById(@PathVariable Long id) {
        LikeResponse like = likeService.getLikeById(id);
        return ResponseEntity.ok(like);
    }

    @GetMapping("/given/user/{userId}")
    @Operation(summary = "Likes donnés par un utilisateur",
            description = "Récupère tous les likes donnés par un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Likes récupérés",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<List<LikeResponse>> getLikesGivenByUser(@PathVariable Long userId) {
        List<LikeResponse> likes = likeService.getLikesGivenByUser(userId);
        return ResponseEntity.ok(likes);
    }

    @GetMapping("/received/user/{userId}")
    @Operation(summary = "Likes reçus par un utilisateur",
            description = "Récupère tous les likes reçus par un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Likes récupérés",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<List<LikeResponse>> getLikesReceivedByUser(@PathVariable Long userId) {
        List<LikeResponse> likes = likeService.getLikesReceivedByUser(userId);
        return ResponseEntity.ok(likes);
    }

    @GetMapping("/matches/user/{userId}")
    @Operation(summary = "Matches d'un utilisateur",
            description = "Récupère tous les matches mutuels d'un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Matches récupérés",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<List<MatchResponse>> getMatchesByUser(@PathVariable Long userId) {
        List<MatchResponse> matches = likeService.getMatchesByUser(userId);
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/check-match")
    @Operation(summary = "Vérifier un match",
            description = "Vérifie si deux utilisateurs se sont mutuellement likés")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vérification effectuée",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class)))
    })
    public ResponseEntity<Boolean> isMatch(
            @Parameter(description = "ID du premier utilisateur", required = true)
            @RequestParam Long userId1,
            @Parameter(description = "ID du second utilisateur", required = true)
            @RequestParam Long userId2) {
        boolean isMatch = likeService.isMatch(userId1, userId2);
        return ResponseEntity.ok(isMatch);
    }

    @GetMapping("/check-like")
    @Operation(summary = "Vérifier un like",
            description = "Vérifie si un utilisateur a déjà liké un autre utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vérification effectuée",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class)))
    })
    public ResponseEntity<Boolean> hasUserLiked(
            @Parameter(description = "ID de l'utilisateur qui like", required = true)
            @RequestParam Long likerId,
            @Parameter(description = "ID de l'utilisateur liké", required = true)
            @RequestParam Long likedUserId) {
        boolean hasLiked = likeService.hasUserLiked(likerId, likedUserId);
        return ResponseEntity.ok(hasLiked);
    }

    @DeleteMapping
    @Operation(summary = "Supprimer un like (unlike)",
            description = "Supprime un like entre deux utilisateurs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Like supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Like non trouvé")
    })
    public ResponseEntity<Void> removeLike(
            @Parameter(description = "ID de l'utilisateur qui avait liké", required = true)
            @RequestParam Long likerId,
            @Parameter(description = "ID de l'utilisateur qui était liké", required = true)
            @RequestParam Long likedUserId) {
        likeService.removeLike(likerId, likedUserId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un like par ID", description = "Supprime un like par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Like supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Like non trouvé")
    })
    public ResponseEntity<Void> removeLikeById(@PathVariable Long id) {
        likeService.removeLike(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats/user/{userId}")
    @Operation(summary = "Statistiques de likes",
            description = "Récupère les statistiques de likes d'un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques récupérées",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LikeStatsResponse.class))),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<LikeStatsResponse> getUserLikeStats(@PathVariable Long userId) {
        LikeStatsResponse stats = likeService.getUserLikeStats(userId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/liked-users/{userId}")
    @Operation(summary = "IDs des utilisateurs likés",
            description = "Récupère les IDs des utilisateurs déjà likés (pour éviter les doublons)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "IDs récupérés",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<List<Long>> getLikedUserIds(@PathVariable Long userId) {
        List<Long> likedUserIds = likeService.getLikedUserIds(userId);
        return ResponseEntity.ok(likedUserIds);
    }

    @GetMapping("/top-users")
    @Operation(summary = "Utilisateurs les plus populaires",
            description = "Récupère les utilisateurs les plus likés")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Top utilisateurs récupérés",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<List<MatchResponse>> getTopLikedUsers(
            @Parameter(description = "Nombre d'utilisateurs à récupérer", example = "10")
            @RequestParam(defaultValue = "10") int limit) {
        List<MatchResponse> topUsers = likeService.getTopLikedUsers(limit);
        return ResponseEntity.ok(topUsers);
    }

    @GetMapping("/recent/user/{userId}")
    @Operation(summary = "Likes récents reçus",
            description = "Récupère les likes récents reçus par un utilisateur (pour notifications)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Likes récents récupérés",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<List<LikeResponse>> getRecentLikesReceived(
            @PathVariable Long userId,
            @Parameter(description = "Nombre d'heures à considérer", example = "24")
            @RequestParam(defaultValue = "24") int hours) {
        List<LikeResponse> recentLikes = likeService.getRecentLikesReceived(userId, hours);
        return ResponseEntity.ok(recentLikes);
    }

    @GetMapping("/count/given/{userId}")
    @Operation(summary = "Nombre de likes donnés",
            description = "Compte le nombre total de likes donnés par un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nombre récupéré",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Long.class)))
    })
    public ResponseEntity<Long> countLikesGivenByUser(@PathVariable Long userId) {
        long count = likeService.countLikesGivenByUser(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/received/{userId}")
    @Operation(summary = "Nombre de likes reçus",
            description = "Compte le nombre total de likes reçus par un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nombre récupéré",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Long.class)))
    })
    public ResponseEntity<Long> countLikesReceivedByUser(@PathVariable Long userId) {
        long count = likeService.countLikesReceivedByUser(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/matches/{userId}")
    @Operation(summary = "Nombre de matches",
            description = "Compte le nombre de matches d'un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nombre récupéré",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Long.class)))
    })
    public ResponseEntity<Long> countMatchesByUser(@PathVariable Long userId) {
        long count = likeService.countMatchesByUser(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/matches/city/{city}")
    @Operation(summary = "Matches par ville",
            description = "Récupère les matches dans une ville spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Matches récupérés",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<List<MatchResponse>> getMatchesByCity(@PathVariable String city) {
        List<MatchResponse> matches = likeService.getMatchesByCity(city);
        return ResponseEntity.ok(matches);
    }

    @DeleteMapping("/user/{userId}/all")
    @Operation(summary = "Supprimer tous les likes d'un utilisateur",
            description = "Supprime tous les likes d'un utilisateur (lors de suppression de compte)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tous les likes supprimés"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<Void> removeAllLikesForUser(@PathVariable Long userId) {
        likeService.removeAllLikesForUser(userId);
        return ResponseEntity.noContent().build();
    }
}