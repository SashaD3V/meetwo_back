// Fichier: InvalidPhotoDataException.java
package com.meetwo.exception.photo;

/**
 * Exception levée pour des erreurs de validation spécifiques aux photos
 */
public class InvalidPhotoDataException extends RuntimeException {

    public InvalidPhotoDataException(String message) {
        super(message);
    }

    public InvalidPhotoDataException(String field, String message) {
        super("Erreur sur le champ '" + field + "' : " + message);
    }
}