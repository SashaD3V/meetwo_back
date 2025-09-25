package com.meetwo.service.impl;

import com.meetwo.dto.mapper.PhotoMapper;
import com.meetwo.dto.request.CreatePhotoRequest;
import com.meetwo.dto.request.UpdatePhotoRequest;
import com.meetwo.dto.response.PhotoResponse;
import com.meetwo.entity.Photo;
import com.meetwo.entity.User;
import com.meetwo.exception.photo.PhotoNotFoundException;
import com.meetwo.exception.photo.MaxPhotosExceededException;
import com.meetwo.exception.user.UserNotFoundException;
import com.meetwo.repository.PhotoRepository;
import com.meetwo.repository.UserRepository;
import com.meetwo.service.PhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PhotoServiceImpl implements PhotoService {

    private final PhotoRepository photoRepository;
    private final UserRepository userRepository;
    private final PhotoMapper photoMapper;

    @Value("${app.upload.dir:uploads/photos}")
    private String uploadDir;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    private static final int MAX_PHOTOS_PER_USER = 6;
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    @Override
    public PhotoResponse createPhoto(CreatePhotoRequest request) {
        log.info("Création d'une photo pour l'utilisateur {}", request.getUserId());

        // Vérifier que l'utilisateur existe
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException(request.getUserId()));

        // Vérifier le nombre maximum de photos
        long photoCount = photoRepository.countByUserId(request.getUserId());
        if (photoCount >= MAX_PHOTOS_PER_USER) {
            throw new MaxPhotosExceededException(MAX_PHOTOS_PER_USER);
        }

        // GESTION DE LA CONTRAINTE PHOTO PRINCIPALE EN JAVA
        Boolean finalEstPrincipale = validateAndManageMainPhoto(request.getUserId(), request.getEstPrincipale(), photoCount);
        request.setEstPrincipale(finalEstPrincipale); // IMPORTANT: Mettre à jour la request

        Photo photo = photoMapper.toEntity(request, user);
        Photo savedPhoto = photoRepository.save(photo);

        log.info("Photo créée avec l'ID {} pour l'utilisateur {}", savedPhoto.getId(), request.getUserId());
        return photoMapper.toResponse(savedPhoto);
    }

    @Override
    public PhotoResponse uploadPhoto(Long userId, Integer position, Boolean estPrincipale, String altText, MultipartFile file) {
        log.info("Upload d'une photo pour l'utilisateur {}", userId);

        // Validations du fichier
        validateFile(file);

        // Vérifier que l'utilisateur existe
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Vérifier le nombre maximum de photos
        long photoCount = photoRepository.countByUserId(userId);
        if (photoCount >= MAX_PHOTOS_PER_USER) {
            throw new MaxPhotosExceededException(MAX_PHOTOS_PER_USER);
        }

        // GESTION DE LA CONTRAINTE PHOTO PRINCIPALE EN JAVA
        Boolean finalEstPrincipale = validateAndManageMainPhoto(userId, estPrincipale, photoCount);

        // Traitement du fichier (sauvegarde et génération de l'URL)
        String photoUrl = saveFileAndGetUrl(file);

        // Créer l'entité Photo AVEC LE USER_ID CORRECT
        Photo photo = new Photo(user, photoUrl, position, finalEstPrincipale);

        // Ajouter les métadonnées du fichier
        photo.setAltText(altText);
        photo.setFileSize(file.getSize());
        photo.setContentType(determineContentType(file));

        // VÉRIFICATION AVANT SAUVEGARDE
        if (photo.getUser() == null) {
            throw new IllegalStateException("User ne peut pas être null lors de la sauvegarde de la photo");
        }

        Photo savedPhoto = photoRepository.save(photo);

        log.info("Photo uploadée avec l'ID {} pour l'utilisateur {}", savedPhoto.getId(), userId);
        return photoMapper.toResponse(savedPhoto);
    }

    /**
     * MÉTHODE CORRIGÉE : Valide et gère la contrainte de photo principale
     * RETOURNE la valeur finale de estPrincipale
     */
    private Boolean validateAndManageMainPhoto(Long userId, Boolean estPrincipale, long currentPhotoCount) {
        // Si c'est la première photo, la définir automatiquement comme principale
        if (currentPhotoCount == 0) {
            log.info("Première photo de l'utilisateur {}, définie comme principale", userId);
            return true; // RETOURNER true
        }

        // Si la photo doit être définie comme principale
        if (Boolean.TRUE.equals(estPrincipale)) {
            // Retirer le statut principal de l'ancienne photo principale
            removeMainPhotoStatus(userId);
            log.info("Nouvelle photo principale définie pour l'utilisateur {}", userId);
            return true; // RETOURNER true
        }

        // Si estPrincipale est null ou false, retourner false
        return false;
    }

    /**
     * Retire le statut de photo principale de manière atomique
     */
    private void removeMainPhotoStatus(Long userId) {
        photoRepository.findByUserIdAndEstPrincipaleTrue(userId)
                .ifPresent(currentMainPhoto -> {
                    currentMainPhoto.setEstPrincipale(false);
                    photoRepository.save(currentMainPhoto);
                    log.debug("Statut principal retiré de la photo {} pour l'utilisateur {}",
                            currentMainPhoto.getId(), userId);
                });
    }

    @Override
    public PhotoResponse updatePhoto(Long id, UpdatePhotoRequest request) {
        log.info("Mise à jour de la photo avec l'ID {}", id);

        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new PhotoNotFoundException(id));

        // GESTION SPÉCIALE POUR LA MISE À JOUR DU STATUT PRINCIPAL
        if (Boolean.TRUE.equals(request.getEstPrincipale())) {
            // Vérifier qu'il n'y a pas déjà une autre photo principale
            photoRepository.findByUserIdAndEstPrincipaleTrue(photo.getUser().getId())
                    .ifPresent(currentMainPhoto -> {
                        if (!currentMainPhoto.getId().equals(id)) {
                            // Retirer le statut principal de l'ancienne photo
                            currentMainPhoto.setEstPrincipale(false);
                            photoRepository.save(currentMainPhoto);
                            log.info("Ancien statut principal retiré de la photo {} pour définir la photo {} comme principale",
                                    currentMainPhoto.getId(), id);
                        }
                    });
        }

        photoMapper.updateEntity(photo, request);
        Photo updatedPhoto = photoRepository.save(photo);

        return photoMapper.toResponse(updatedPhoto);
    }

    @Override
    public PhotoResponse setAsMainPhoto(Long photoId) {
        log.info("Définition de la photo {} comme photo principale", photoId);

        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new PhotoNotFoundException(photoId));

        // VÉRIFICATION ET GESTION DE LA CONTRAINTE UNIQUE
        Long userId = photo.getUser().getId();

        // Retirer le statut principal de l'ancienne photo principale
        photoRepository.findByUserIdAndEstPrincipaleTrue(userId)
                .ifPresent(currentMainPhoto -> {
                    if (!currentMainPhoto.getId().equals(photoId)) {
                        currentMainPhoto.setEstPrincipale(false);
                        photoRepository.save(currentMainPhoto);
                        log.info("Statut principal transféré de la photo {} vers la photo {}",
                                currentMainPhoto.getId(), photoId);
                    }
                });

        // Définir la nouvelle photo comme principale
        photo.setEstPrincipale(true);
        Photo updatedPhoto = photoRepository.save(photo);

        return photoMapper.toResponse(updatedPhoto);
    }

    @Override
    public void deletePhoto(Long id) {
        log.info("Suppression de la photo avec l'ID {}", id);

        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new PhotoNotFoundException(id));

        boolean wasMainPhoto = photo.getEstPrincipale();
        Long userId = photo.getUser().getId();

        // Supprimer le fichier physique
        deletePhysicalFile(photo.getUrl());

        // Supprimer de la base de données
        photoRepository.deleteById(id);
        log.info("Photo {} supprimée", id);

        // Si c'était la photo principale, définir une nouvelle photo principale
        if (wasMainPhoto) {
            setNewMainPhotoAfterDeletion(userId);
        }
    }

    /**
     * Définit automatiquement une nouvelle photo principale après suppression
     */
    private void setNewMainPhotoAfterDeletion(Long userId) {
        List<Photo> remainingPhotos = photoRepository.findByUserIdOrderByPositionAsc(userId);
        if (!remainingPhotos.isEmpty()) {
            Photo newMainPhoto = remainingPhotos.get(0); // Prendre la première par position
            newMainPhoto.setEstPrincipale(true);
            photoRepository.save(newMainPhoto);
            log.info("Nouvelle photo principale automatiquement définie (ID: {}) pour l'utilisateur {}",
                    newMainPhoto.getId(), userId);
        } else {
            log.info("Aucune photo restante pour l'utilisateur {} après suppression", userId);
        }
    }

    // === MÉTHODES DE VALIDATION ET UTILITAIRES ===

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier ne peut pas être vide");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Le fichier est trop volumineux. Taille maximum: " + (MAX_FILE_SIZE / 1024 / 1024) + "MB");
        }

        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || originalFilename.contains("..")) {
            throw new IllegalArgumentException("Nom de fichier invalide");
        }

        if (!isValidImageFile(contentType, originalFilename)) {
            throw new IllegalArgumentException("Type de fichier non supporté. Types autorisés: jpg, jpeg, png, gif, webp");
        }
    }

    private boolean isValidImageFile(String contentType, String filename) {
        List<String> allowedExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".webp");

        boolean validExtension = false;
        if (filename != null) {
            String lowerFilename = filename.toLowerCase();
            validExtension = allowedExtensions.stream().anyMatch(lowerFilename::endsWith);
        }

        boolean validContentType = contentType != null && ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase());

        if (validExtension) {
            if (contentType != null &&
                    (contentType.equals("application/octet-stream") || contentType.equals("binary/octet-stream"))) {
                log.warn("Content-Type générique détecté ({}), validation basée sur l'extension: {}", contentType, filename);
                return true;
            }
            return validContentType || contentType == null;
        }

        return validContentType;
    }

    private String determineContentType(MultipartFile file) {
        String originalContentType = file.getContentType();
        String filename = file.getOriginalFilename();

        if (filename != null) {
            String lowerFilename = filename.toLowerCase();
            if (lowerFilename.endsWith(".jpg") || lowerFilename.endsWith(".jpeg")) {
                return "image/jpeg";
            } else if (lowerFilename.endsWith(".png")) {
                return "image/png";
            } else if (lowerFilename.endsWith(".gif")) {
                return "image/gif";
            } else if (lowerFilename.endsWith(".webp")) {
                return "image/webp";
            }
        }

        return originalContentType != null ? originalContentType : "image/jpeg";
    }

    private String saveFileAndGetUrl(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String uniqueFilename = UUID.randomUUID().toString() + "_" + System.currentTimeMillis() + fileExtension;

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String photoUrl = baseUrl + "/uploads/" + uniqueFilename;
            log.info("Fichier sauvegardé: {} -> URL: {}", filePath.toString(), photoUrl);

            return photoUrl;

        } catch (IOException e) {
            log.error("Erreur lors de la sauvegarde du fichier: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de l'upload du fichier: " + e.getMessage(), e);
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    private void deletePhysicalFile(String photoUrl) {
        try {
            if (photoUrl != null && photoUrl.startsWith(baseUrl + "/uploads/")) {
                String filename = photoUrl.substring(photoUrl.lastIndexOf("/") + 1);
                Path filePath = Paths.get(uploadDir, filename);

                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                    log.info("Fichier physique supprimé: {}", filePath.toString());
                }
            }
        } catch (IOException e) {
            log.warn("Impossible de supprimer le fichier physique: {}", e.getMessage());
        }
    }

    // === MÉTHODES DE LECTURE (READ-ONLY) ===

    @Override
    @Transactional(readOnly = true)
    public PhotoResponse getPhotoById(Long id) {
        log.debug("Récupération de la photo avec l'ID {}", id);

        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new PhotoNotFoundException(id));
        return photoMapper.toResponse(photo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PhotoResponse> getPhotosByUserId(Long userId) {
        log.debug("Récupération des photos de l'utilisateur {}", userId);

        return photoRepository.findByUserIdOrderByPositionAsc(userId).stream()
                .map(photoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PhotoResponse getMainPhotoByUserId(Long userId) {
        log.debug("Récupération de la photo principale de l'utilisateur {}", userId);

        Photo photo = photoRepository.findByUserIdAndEstPrincipaleTrue(userId)
                .orElseThrow(() -> new PhotoNotFoundException("Aucune photo principale pour l'utilisateur " + userId));
        return photoMapper.toResponse(photo);
    }

    @Override
    public void reorderPhotos(Long userId, List<Long> photoIds) {
        log.info("Réorganisation des photos pour l'utilisateur {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        for (int i = 0; i < photoIds.size(); i++) {
            final Long photoId = photoIds.get(i);
            Photo photo = photoRepository.findById(photoId)
                    .orElseThrow(() -> new PhotoNotFoundException(photoId));

            if (!photo.getUser().getId().equals(userId)) {
                throw new IllegalArgumentException("La photo " + photoId +
                        " n'appartient pas à l'utilisateur " + userId);
            }

            photo.setPosition(i + 1);
            photoRepository.save(photo);
        }

        log.info("Photos réorganisées pour l'utilisateur {}", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countPhotosByUserId(Long userId) {
        return photoRepository.countByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasMainPhoto(Long userId) {
        return photoRepository.hasMainPhoto(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public String getMainPhotoUrl(Long userId) {
        return photoRepository.findByUserIdAndEstPrincipaleTrue(userId)
                .map(Photo::getUrl)
                .orElse(null);
    }
}