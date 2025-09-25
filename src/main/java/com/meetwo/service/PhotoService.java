package com.meetwo.service;

import com.meetwo.dto.request.CreatePhotoRequest;
import com.meetwo.dto.request.UpdatePhotoRequest;
import com.meetwo.dto.response.PhotoResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service pour la gestion des photos des utilisateurs
 */
public interface PhotoService {

    /**
     * Upload et crée une nouvelle photo pour un utilisateur
     */
    PhotoResponse uploadPhoto(Long userId, Integer position, Boolean estPrincipale, String altText, MultipartFile file);

    /**
     * Crée une nouvelle photo pour un utilisateur
     */
    PhotoResponse createPhoto(CreatePhotoRequest request);

    /**
     * Récupère une photo par son ID
     */
    PhotoResponse getPhotoById(Long id);

    /**
     * Récupère toutes les photos d'un utilisateur ordonnées par position
     */
    List<PhotoResponse> getPhotosByUserId(Long userId);

    /**
     * Récupère la photo principale d'un utilisateur
     */
    PhotoResponse getMainPhotoByUserId(Long userId);

    /**
     * Met à jour une photo existante
     */
    PhotoResponse updatePhoto(Long id, UpdatePhotoRequest request);

    /**
     * Supprime une photo
     */
    void deletePhoto(Long id);

    /**
     * Définit une photo comme photo principale
     */
    PhotoResponse setAsMainPhoto(Long photoId);

    /**
     * Réorganise l'ordre des photos d'un utilisateur
     */
    void reorderPhotos(Long userId, List<Long> photoIds);

    /**
     * Compte le nombre de photos d'un utilisateur
     */
    long countPhotosByUserId(Long userId);

    /**
     * Vérifie si un utilisateur a une photo principale
     */
    boolean hasMainPhoto(Long userId);

    /**
     * Récupère l'URL de la photo principale d'un utilisateur
     */
    String getMainPhotoUrl(Long userId);
}