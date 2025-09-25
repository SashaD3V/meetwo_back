package com.meetwo.exception.photo;

/**
 * Exception levée quand un utilisateur tente d'ajouter plus de photos que le maximum autorisé
 */
public class MaxPhotosExceededException extends RuntimeException {

    public MaxPhotosExceededException(int maxPhotos) {
        super("Nombre maximum de photos atteint (" + maxPhotos + ")");
    }

    public MaxPhotosExceededException(int maxPhotos, int currentCount) {
        super("Nombre maximum de photos atteint (" + maxPhotos + "). Vous avez déjà " + currentCount + " photos.");
    }
}