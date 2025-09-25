package com.meetwo.exception.message;

import lombok.Getter;

/**
 * Exception levée quand une opération message n'est pas autorisée
 */
@Getter
public class InvalidMessageOperationException extends RuntimeException {

    private final String operation;
    private final String reason;

    public InvalidMessageOperationException(String message) {
        super(message);
        this.operation = null;
        this.reason = null;
    }

    public InvalidMessageOperationException(String operation, String reason) {
        super("Opération '" + operation + "' non autorisée : " + reason);
        this.operation = operation;
        this.reason = reason;
    }
}