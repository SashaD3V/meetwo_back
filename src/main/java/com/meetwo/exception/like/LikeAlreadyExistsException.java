package com.meetwo.exception.like;

public class LikeAlreadyExistsException extends RuntimeException {

    public LikeAlreadyExistsException(Long likerId, Long likedUserId) {
        super("L'utilisateur " + likerId + " a déjà liké l'utilisateur " + likedUserId);
    }

    public LikeAlreadyExistsException(String message) {
        super(message);
    }
}