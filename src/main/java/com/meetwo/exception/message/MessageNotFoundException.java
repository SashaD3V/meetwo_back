package com.meetwo.exception.message;

import lombok.Getter;

@Getter
public class MessageNotFoundException extends RuntimeException {

    private final Long messageId;

    public MessageNotFoundException(Long id) {
        super("Message non trouvé avec l'ID : " + id);
        this.messageId = id;
    }

    public MessageNotFoundException(String message) {
        super(message);
        this.messageId = null;
    }
}