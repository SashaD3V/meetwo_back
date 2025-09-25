package com.meetwo.exception.message;

import lombok.Getter;

@Getter
public class ConversationNotFoundException extends RuntimeException {

    private final Long userId1;
    private final Long userId2;

    public ConversationNotFoundException(Long userId1, Long userId2) {
        super("Aucune conversation trouvée entre les utilisateurs " + userId1 + " et " + userId2);
        this.userId1 = userId1;
        this.userId2 = userId2;
    }

    public ConversationNotFoundException(String message) {
        super(message);
        this.userId1 = null;
        this.userId2 = null;
    }
}