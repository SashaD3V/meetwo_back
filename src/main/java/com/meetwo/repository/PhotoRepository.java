package com.meetwo.repository;

import com.meetwo.entity.Photo;
import com.meetwo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

    // Trouver toutes les photos d'un utilisateur
    List<Photo> findByUserIdOrderByPositionAsc(Long userId);

    // Trouver la photo principale d'un utilisateur
    Optional<Photo> findByUserIdAndEstPrincipaleTrue(Long userId);

    // Trouver toutes les photos principales
    @Query("SELECT p FROM Photo p WHERE p.estPrincipale = true")
    List<Photo> findAllMainPhotos();

    // Compter les photos d'un utilisateur
    long countByUserId(Long userId);

    // Trouver la position maximale pour un utilisateur
    @Query("SELECT COALESCE(MAX(p.position), 0) FROM Photo p WHERE p.user.id = :userId")
    Integer findMaxPositionByUserId(@Param("userId") Long userId);

    // Trouver les photos par position
    Optional<Photo> findByUserIdAndPosition(Long userId, Integer position);

    // Supprimer toutes les photos d'un utilisateur
    void deleteByUserId(Long userId);

    // Vérifier si un utilisateur a une photo principale
    @Query("SELECT COUNT(p) > 0 FROM Photo p WHERE p.user.id = :userId AND p.estPrincipale = true")
    boolean hasMainPhoto(@Param("userId") Long userId);
}
