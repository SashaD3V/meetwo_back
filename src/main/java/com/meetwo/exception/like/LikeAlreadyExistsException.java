package com.meetwo.exception.like;

public class LikeAlreadyExistsException extends RuntimeException {
  public LikeAlreadyExistsException(String message) {
    super(message);
  }
}
