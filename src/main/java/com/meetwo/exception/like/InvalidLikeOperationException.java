package com.meetwo.exception.like;

/**
 * Exception levée quand une opération like n'est pas autorisée
 */
public class InvalidLikeOperationException extends RuntimeException {

    public InvalidLikeOperationException(String message) {
        super(message);
    }

    public InvalidLikeOperationException(String operation, String reason) {
        super("Opération '" + operation + "' non autorisée : " + reason);
    }
}