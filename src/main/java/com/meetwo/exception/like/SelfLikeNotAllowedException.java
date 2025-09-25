package com.meetwo.exception.like;

public class SelfLikeNotAllowedException extends RuntimeException {

    public SelfLikeNotAllowedException() {
        super("Un utilisateur ne peut pas se liker lui-même");
    }

    public SelfLikeNotAllowedException(Long userId) {
        super("L'utilisateur " + userId + " ne peut pas se liker lui-même");
    }
}