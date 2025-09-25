package com.meetwo.exception.like;

public class LikeNotFoundException extends RuntimeException {

  public LikeNotFoundException(Long id) {
    super("Like non trouvé avec l'ID : " + id);
  }

  public LikeNotFoundException(String message) {
    super(message);
  }

  public LikeNotFoundException(Long likerId, Long likedUserId) {
    super("Like non trouvé entre l'utilisateur " + likerId + " et l'utilisateur " + likedUserId);
  }
}