package com.meetwo.service;

import com.meetwo.dto.request.CreateLikeRequest;
import com.meetwo.dto.request.UpdateLikeRequest;
import com.meetwo.dto.response.LikeResponse;
import com.meetwo.dto.response.MatchResponse;
import com.meetwo.dto.response.LikeStatsResponse;

import java.util.List;

/**
 * Service pour la gestion des likes dans l'application de rencontre
 */
public interface LikeService {

    /**
     * Crée un nouveau like
     */
    LikeResponse createLike(CreateLikeRequest request);

    /**
     * Like rapide avec juste les IDs
     */
    LikeResponse likeUser(Long likerId, Long likedUserId);

    /**
     * Met à jour un like existant
     */
    LikeResponse updateLike(Long id, UpdateLikeRequest request);

    /**
     * Récupère un like par son ID
     */
    LikeResponse getLikeById(Long id);

    /**
     * Récupère tous les likes donnés par un utilisateur
     */
    List<LikeResponse> getLikesGivenByUser(Long userId);

    /**
     * Récupère tous les likes reçus par un utilisateur
     */
    List<LikeResponse> getLikesReceivedByUser(Long userId);

    /**
     * Récupère les matches mutuels d'un utilisateur
     */
    List<MatchResponse> getMatchesByUser(Long userId);

    /**
     * Vérifie si deux utilisateurs se sont mutuellement likés (match)
     */
    boolean isMatch(Long userId1, Long userId2);

    /**
     * Vérifie si un utilisateur a déjà liké un autre utilisateur
     */
    boolean hasUserLiked(Long likerId, Long likedUserId);

    /**
     * Supprime un like (unlike)
     */
    void removeLike(Long likerId, Long likedUserId);

    /**
     * Supprime un like par son ID
     */
    void removeLike(Long likeId);

    /**
     * Récupère les statistiques de likes d'un utilisateur
     */
    LikeStatsResponse getUserLikeStats(Long userId);

    /**
     * Récupère les IDs des utilisateurs déjà likés (pour éviter de les reproposer)
     */
    List<Long> getLikedUserIds(Long likerId);

    /**
     * Récupère les utilisateurs les plus populaires (les plus likés)
     */
    List<MatchResponse> getTopLikedUsers(int limit);

    /**
     * Récupère les likes récents reçus par un utilisateur (pour notifications)
     */
    List<LikeResponse> getRecentLikesReceived(Long userId, int hours);

    /**
     * Compte le nombre total de likes donnés par un utilisateur
     */
    long countLikesGivenByUser(Long userId);

    /**
     * Compte le nombre total de likes reçus par un utilisateur
     */
    long countLikesReceivedByUser(Long userId);

    /**
     * Compte le nombre de matches d'un utilisateur
     */
    long countMatchesByUser(Long userId);

    /**
     * Récupère les matches dans une ville spécifique
     */
    List<MatchResponse> getMatchesByCity(String city);

    /**
     * Supprime tous les likes d'un utilisateur (lors de suppression de compte)
     */
    void removeAllLikesForUser(Long userId);
}