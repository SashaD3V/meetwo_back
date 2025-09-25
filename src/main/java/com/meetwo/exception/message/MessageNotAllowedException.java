package com.meetwo.exception.message;

import lombok.Getter;

/**
 * Exception levée quand un utilisateur n'est pas autorisé à envoyer un message
 * (par exemple, s'il n'y a pas de match mutuel)
 */
@Getter
public class MessageNotAllowedException extends RuntimeException {

    private final Long senderId;
    private final Long receiverId;

    public MessageNotAllowedException(String message) {
        super(message);
        this.senderId = null;
        this.receiverId = null;
    }

    public MessageNotAllowedException(Long senderId, Long receiverId) {
        super("L'utilisateur " + senderId + " n'est pas autorisé à envoyer un message à l'utilisateur " + receiverId);
        this.senderId = senderId;
        this.receiverId = receiverId;
    }
}