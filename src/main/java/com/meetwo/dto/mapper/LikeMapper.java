package com.meetwo.dto.mapper;

import com.meetwo.dto.request.CreateLikeRequest;
import com.meetwo.dto.request.UpdateLikeRequest;
import com.meetwo.dto.response.LikeResponse;
import com.meetwo.dto.response.MatchResponse;
import com.meetwo.dto.response.LikeStatsResponse;
import com.meetwo.entity.Like;
import com.meetwo.entity.User;
import com.meetwo.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class LikeMapper {

    private final PhotoService photoService;

    public Like toEntity(CreateLikeRequest request, User liker, User likedUser) {
        Like like = new Like();
        like.setLiker(liker);
        like.setLikedUser(likedUser);
        return like;
    }

    public LikeResponse toResponse(Like like) {
        LikeResponse response = new LikeResponse();
        response.setId(like.getId());

        // Informations du liker
        response.setLikerId(like.getLiker().getId());
        response.setLikerUsername(like.getLiker().getUsername());
        response.setLikerName(like.getLiker().getName());
        response.setLikerMainPhotoUrl(getMainPhotoUrl(like.getLiker().getId()));

        // Informations du liké
        response.setLikedUserId(like.getLikedUser().getId());
        response.setLikedUserUsername(like.getLikedUser().getUsername());
        response.setLikedUserName(like.getLikedUser().getName());
        response.setLikedUserMainPhotoUrl(getMainPhotoUrl(like.getLikedUser().getId()));

        response.setCreatedAt(like.getCreatedAt());
        response.setUpdatedAt(like.getUpdatedAt());

        return response;
    }

    public LikeResponse toResponseWithMatch(Like like, boolean isMatch) {
        LikeResponse response = toResponse(like);
        response.setMatch(isMatch);
        return response;
    }

    public MatchResponse toMatchResponse(Like like, LocalDateTime matchedAt) {
        MatchResponse response = new MatchResponse();

        // Déterminer quel utilisateur afficher (l'autre que celui qui fait la requête)
        User matchedUser = like.getLikedUser(); // Par défaut, on suppose que c'est la personne likée

        response.setMatchedUserId(matchedUser.getId());
        response.setUsername(matchedUser.getUsername());
        response.setName(matchedUser.getName());
        response.setAge(matchedUser.getAge());
        response.setCity(matchedUser.getCity());
        response.setBiography(matchedUser.getBiography());
        response.setMainPhotoUrl(getMainPhotoUrl(matchedUser.getId()));
        response.setMatchedAt(matchedAt);
        response.setHasUnreadMessages(false); // À implémenter avec le système de chat

        return response;
    }

    public MatchResponse toMatchResponse(User matchedUser, LocalDateTime matchedAt) {
        MatchResponse response = new MatchResponse();

        response.setMatchedUserId(matchedUser.getId());
        response.setUsername(matchedUser.getUsername());
        response.setName(matchedUser.getName());
        response.setAge(matchedUser.getAge());
        response.setCity(matchedUser.getCity());
        response.setBiography(matchedUser.getBiography());
        response.setMainPhotoUrl(getMainPhotoUrl(matchedUser.getId()));
        response.setMatchedAt(matchedAt);
        response.setHasUnreadMessages(false);

        return response;
    }

    public LikeStatsResponse toStatsResponse(Long userId, long likesGiven, long likesReceived, long matchesCount) {
        LikeStatsResponse response = new LikeStatsResponse();
        response.setUserId(userId);
        response.setLikesGiven(likesGiven);
        response.setLikesReceived(likesReceived);
        response.setMatchesCount(matchesCount);

        // Calcul du taux de retour des likes
        if (likesGiven > 0) {
            response.setLikeBackRate((double) matchesCount / likesGiven * 100);
        } else {
            response.setLikeBackRate(0.0);
        }

        // Score de popularité simple basé sur les likes reçus
        response.setPopularityScore(calculatePopularityScore(likesReceived));

        return response;
    }

    private String getMainPhotoUrl(Long userId) {
        try {
            return photoService.getMainPhotoUrl(userId);
        } catch (Exception e) {
            return null; // Si pas de photo principale
        }
    }

    public void updateEntity(Like like, UpdateLikeRequest request) {
        if (request == null || like == null) return;

        // Pour l'instant, il n'y a pas de champs à mettre à jour dans un Like
        // mais on garde la méthode pour la cohérence avec les autres mappers
        // et pour d'éventuelles futures fonctionnalités

        if (request.getNote() != null) {
            // Fonctionnalité future : ajouter une note privée au like
            // like.setNote(request.getNote());
        }

        if (request.getIsHidden() != null) {
            // Fonctionnalité future : masquer le like
            // like.setHidden(request.getIsHidden());
        }
    }

    private double calculatePopularityScore(long likesReceived) {
        // Score simple : logarithme naturel des likes + 1 pour éviter 0
        if (likesReceived <= 0) {
            return 0.0;
        }
        return Math.log(likesReceived + 1) * 10; // Multiplié par 10 pour avoir un score plus lisible
    }
}