package com.meetwo.repository;

import com.meetwo.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    // Vérifier si un utilisateur a déjà liké un autre utilisateur
    boolean existsByLikerIdAndLikedUserId(Long likerId, Long likedUserId);

    // Trouver un like spécifique
    Optional<Like> findByLikerIdAndLikedUserId(Long likerId, Long likedUserId);

    // Récupérer tous les likes donnés par un utilisateur
    List<Like> findByLikerIdOrderByCreatedAtDesc(Long likerId);

    // Récupérer tous les likes reçus par un utilisateur
    List<Like> findByLikedUserIdOrderByCreatedAtDesc(Long likedUserId);

    // Compter les likes donnés par un utilisateur
    long countByLikerId(Long likerId);

    // Compter les likes reçus par un utilisateur
    long countByLikedUserId(Long likedUserId);

    // Récupérer les likes récents d'un utilisateur (pour éviter de proposer les mêmes profils)
    @Query("SELECT l FROM Like l WHERE l.liker.id = :likerId ORDER BY l.createdAt DESC LIMIT :limit")
    List<Like> findRecentLikesByLiker(@Param("likerId") Long likerId, @Param("limit") int limit);

    // Récupérer les likes mutuels (matches)
    @Query("SELECT l1 FROM Like l1 " +
            "WHERE EXISTS (SELECT l2 FROM Like l2 " +
            "WHERE l1.liker.id = l2.likedUser.id " +
            "AND l1.likedUser.id = l2.liker.id) " +
            "AND l1.liker.id = :userId " +
            "ORDER BY l1.createdAt DESC")
    List<Like> findMutualLikesByUser(@Param("userId") Long userId);

    // Vérifier s'il y a un match mutuel entre deux utilisateurs
    @Query("SELECT COUNT(l) > 0 FROM Like l " +
            "WHERE (l.liker.id = :userId1 AND l.likedUser.id = :userId2) " +
            "OR (l.liker.id = :userId2 AND l.likedUser.id = :userId1)")
    boolean existsMutualLike(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    // Récupérer les IDs des utilisateurs déjà likés par un utilisateur
    @Query("SELECT l.likedUser.id FROM Like l WHERE l.liker.id = :likerId")
    List<Long> findLikedUserIdsByLiker(@Param("likerId") Long likerId);

    // Récupérer les likes récents reçus par un utilisateur (notifications)
    @Query("SELECT l FROM Like l WHERE l.likedUser.id = :userId " +
            "AND l.createdAt >= :since ORDER BY l.createdAt DESC")
    List<Like> findRecentLikesReceived(@Param("userId") Long userId, @Param("since") java.time.LocalDateTime since);

    // Supprimer un like spécifique
    void deleteByLikerIdAndLikedUserId(Long likerId, Long likedUserId);

    // Supprimer tous les likes d'un utilisateur (quand il supprime son compte)
    void deleteByLikerIdOrLikedUserId(Long userId, Long userId2);

    // Statistiques : Top des utilisateurs les plus likés
    @Query("SELECT l.likedUser.id, COUNT(l) as likeCount FROM Like l " +
            "GROUP BY l.likedUser.id ORDER BY likeCount DESC LIMIT :limit")
    List<Object[]> findTopLikedUsers(@Param("limit") int limit);

    // Récupérer les likes entre utilisateurs d'une même ville (pour matching local)
    @Query("SELECT l FROM Like l " +
            "WHERE l.liker.city = l.likedUser.city " +
            "AND l.liker.city = :city " +
            "ORDER BY l.createdAt DESC")
    List<Like> findLikesByCity(@Param("city") String city);

    // Vérifier si c'est un match mutuel complet
    @Query("SELECT COUNT(l) FROM Like l " +
            "WHERE (l.liker.id = :userId1 AND l.likedUser.id = :userId2) " +
            "AND EXISTS (SELECT l2 FROM Like l2 " +
            "WHERE l2.liker.id = :userId2 AND l2.likedUser.id = :userId1)")
    long countMutualLikes(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}