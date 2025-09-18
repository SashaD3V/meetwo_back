package com.meetwo.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileUploadService {
    
    /**
     * Télécharge une image de profil
     * @param userId l'identifiant de l'utilisateur
     * @param file le fichier à télécharger
     * @return l'URL de l'image téléchargée
     * @throws IOException en cas d'erreur lors du téléchargement
     */
    String uploadProfileImage(Long userId, MultipartFile file) throws IOException;
    
    /**
     * Télécharge une image de message
     * @param userId l'identifiant de l'utilisateur
     * @param file le fichier à télécharger
     * @return l'URL de l'image téléchargée
     * @throws IOException en cas d'erreur lors du téléchargement
     */
    String uploadMessageImage(Long userId, MultipartFile file) throws IOException;
    
    /**
     * Supprime une image
     * @param imageUrl l'URL de l'image à supprimer
     * @return true si l'image a été supprimée, false sinon
     */
    boolean deleteImage(String imageUrl);
}