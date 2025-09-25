package com.meetwo.exception.photo;

/**
 * Exception levée quand une opération photo n'est pas autorisée
 */
public class PhotoOperationNotAllowedException extends RuntimeException {

    public PhotoOperationNotAllowedException(String message) {
        super(message);
    }

    public PhotoOperationNotAllowedException(String operation, String reason) {
        super("Opération '" + operation + "' non autorisée : " + reason);
    }
}