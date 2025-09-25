package com.meetwo.exception.photo;

public class PhotoNotFoundException extends RuntimeException {

    public PhotoNotFoundException(Long id) {
        super("Photo non trouvée avec l'ID : " + id);
    }

    public PhotoNotFoundException(String message) {
        super(message);
    }

    public PhotoNotFoundException(String field, String value) {
        super("Photo non trouvée avec " + field + " : " + value);
    }
}