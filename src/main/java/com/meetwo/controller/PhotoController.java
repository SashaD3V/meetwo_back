package com.meetwo.controller;

import com.meetwo.dto.request.CreatePhotoRequest;
import com.meetwo.dto.request.UpdatePhotoRequest;
import com.meetwo.dto.response.PhotoResponse;
import com.meetwo.service.PhotoService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
@Tag(name = "Photos", description = "API de gestion des photos des utilisateurs")
public class PhotoController {

    private final PhotoService photoService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload d'une photo", description = "Upload et ajoute une nouvelle photo pour un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Photo uploadée avec succès",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PhotoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Fichier invalide ou données manquantes"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
            @ApiResponse(responseCode = "409", description = "Nombre maximum de photos atteint"),
            @ApiResponse(responseCode = "413", description = "Fichier trop volumineux")
    })
    public ResponseEntity<PhotoResponse> uploadPhoto(
            @Parameter(description = "ID de l'utilisateur", required = true)
            @RequestParam("userId") Long userId,

            @Parameter(description = "Position de la photo", required = true)
            @RequestParam("position") Integer position,

            @Parameter(description = "Définir comme photo principale")
            @RequestParam(value = "estPrincipale", required = false, defaultValue = "false") Boolean estPrincipale,

            @Parameter(description = "Texte alternatif pour l'accessibilité")
            @RequestParam(value = "altText", required = false) String altText,

            @Parameter(
                    description = "Fichier image à uploader (JPEG, PNG uniquement)",
                    required = true
            )
            @RequestPart("file")
            @Schema(type = "string", format = "binary")
            MultipartFile file) {

        PhotoResponse photo = photoService.uploadPhoto(userId, position, estPrincipale, altText, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(photo);
    }

    @PostMapping("/url")
    @Operation(summary = "Ajouter une photo par URL", description = "Ajoute une nouvelle photo pour un utilisateur via URL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Photo ajoutée avec succès",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PhotoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
            @ApiResponse(responseCode = "409", description = "Nombre maximum de photos atteint")
    })
    public ResponseEntity<PhotoResponse> createPhoto(@Valid @RequestBody CreatePhotoRequest request) {
        PhotoResponse photo = photoService.createPhoto(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(photo);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une photo", description = "Récupère une photo par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Photo trouvée",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PhotoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Photo non trouvée")
    })
    public ResponseEntity<PhotoResponse> getPhotoById(@PathVariable Long id) {
        PhotoResponse photo = photoService.getPhotoById(id);
        return ResponseEntity.ok(photo);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Photos d'un utilisateur", description = "Récupère toutes les photos d'un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Photos récupérées",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<List<PhotoResponse>> getPhotosByUserId(@PathVariable Long userId) {
        List<PhotoResponse> photos = photoService.getPhotosByUserId(userId);
        return ResponseEntity.ok(photos);
    }

    @GetMapping("/user/{userId}/main")
    @Operation(summary = "Photo principale", description = "Récupère la photo principale d'un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Photo principale trouvée",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PhotoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Aucune photo principale trouvée")
    })
    public ResponseEntity<PhotoResponse> getMainPhoto(@PathVariable Long userId) {
        PhotoResponse photo = photoService.getMainPhotoByUserId(userId);
        return ResponseEntity.ok(photo);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une photo", description = "Met à jour les informations d'une photo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Photo modifiée avec succès",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PhotoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Photo non trouvée")
    })
    public ResponseEntity<PhotoResponse> updatePhoto(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePhotoRequest request) {
        PhotoResponse photo = photoService.updatePhoto(id, request);
        return ResponseEntity.ok(photo);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une photo", description = "Supprime une photo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Photo supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Photo non trouvée")
    })
    public ResponseEntity<Void> deletePhoto(@PathVariable Long id) {
        photoService.deletePhoto(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/set-main")
    @Operation(summary = "Définir comme photo principale", description = "Définit une photo comme principale")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Photo définie comme principale",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PhotoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Photo non trouvée")
    })
    public ResponseEntity<PhotoResponse> setAsMainPhoto(@PathVariable Long id) {
        PhotoResponse photo = photoService.setAsMainPhoto(id);
        return ResponseEntity.ok(photo);
    }

    @PutMapping("/user/{userId}/reorder")
    @Operation(summary = "Réorganiser les photos", description = "Change l'ordre des photos d'un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Photos réorganisées avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
            @ApiResponse(responseCode = "400", description = "Liste d'IDs invalide")
    })
    public ResponseEntity<Void> reorderPhotos(
            @PathVariable Long userId,
            @RequestBody List<Long> photoIds) {
        photoService.reorderPhotos(userId, photoIds);
        return ResponseEntity.ok().build();
    }
}