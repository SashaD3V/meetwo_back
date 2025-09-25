package com.meetwo.exception;

import com.meetwo.exception.user.UserAlreadyExistsException;
import com.meetwo.exception.user.UserNotFoundException;
import com.meetwo.exception.user.InvalidUserDataException;
import com.meetwo.exception.like.LikeNotFoundException;
import com.meetwo.exception.like.LikeAlreadyExistsException;
import com.meetwo.exception.like.SelfLikeNotAllowedException;
import com.meetwo.exception.like.InvalidLikeOperationException;
import com.meetwo.exception.photo.PhotoNotFoundException;
import com.meetwo.exception.photo.MaxPhotosExceededException;
import com.meetwo.exception.photo.PhotoOperationNotAllowedException;
import com.meetwo.exception.photo.InvalidPhotoDataException;
import com.meetwo.exception.message.MessageNotFoundException;
import com.meetwo.exception.message.InvalidMessageOperationException;
import com.meetwo.exception.message.ConversationNotFoundException;
import com.meetwo.exception.message.MessageNotAllowedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // === EXCEPTIONS UTILISATEUR ===

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidUserDataException.class)
    public ResponseEntity<String> handleInvalidUserData(InvalidUserDataException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    // === EXCEPTIONS LIKE ===

    @ExceptionHandler(LikeNotFoundException.class)
    public ResponseEntity<String> handleLikeNotFound(LikeNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(LikeAlreadyExistsException.class)
    public ResponseEntity<String> handleLikeAlreadyExists(LikeAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(SelfLikeNotAllowedException.class)
    public ResponseEntity<String> handleSelfLikeNotAllowed(SelfLikeNotAllowedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidLikeOperationException.class)
    public ResponseEntity<String> handleInvalidLikeOperation(InvalidLikeOperationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    // === EXCEPTIONS PHOTO ===

    @ExceptionHandler(PhotoNotFoundException.class)
    public ResponseEntity<String> handlePhotoNotFound(PhotoNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(MaxPhotosExceededException.class)
    public ResponseEntity<String> handleMaxPhotosExceeded(MaxPhotosExceededException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(PhotoOperationNotAllowedException.class)
    public ResponseEntity<String> handlePhotoOperationNotAllowed(PhotoOperationNotAllowedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidPhotoDataException.class)
    public ResponseEntity<String> handleInvalidPhotoData(InvalidPhotoDataException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    // === EXCEPTIONS MESSAGE ===

    @ExceptionHandler(MessageNotFoundException.class)
    public ResponseEntity<String> handleMessageNotFound(MessageNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidMessageOperationException.class)
    public ResponseEntity<String> handleInvalidMessageOperation(InvalidMessageOperationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(ConversationNotFoundException.class)
    public ResponseEntity<String> handleConversationNotFound(ConversationNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(MessageNotAllowedException.class)
    public ResponseEntity<String> handleMessageNotAllowed(MessageNotAllowedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    // === EXCEPTIONS GÉNÉRIQUES ===

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleEnumConversionError(
            MethodArgumentTypeMismatchException ex) {

        Map<String, String> error = new HashMap<>();

        if ("gender".equals(ex.getName())) {
            error.put("error", "Invalid gender value: " + String.valueOf(ex.getValue()));
            error.put("message", "Gender must be either 'HOMME' or 'FEMME'");
            error.put("received", String.valueOf(ex.getValue()));
            error.put("validValues", "HOMME, FEMME");
        } else if ("messageType".equals(ex.getName())) {
            error.put("error", "Invalid message type: " + String.valueOf(ex.getValue()));
            error.put("message", "Message type must be 'TEXT', 'IMAGE', or 'SYSTEM'");
            error.put("received", String.valueOf(ex.getValue()));
            error.put("validValues", "TEXT, IMAGE, SYSTEM");
        } else {
            error.put("error", "Invalid parameter: " + ex.getName());
            error.put("message", "Parameter '" + ex.getName() + "' has invalid value: " + String.valueOf(ex.getValue()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Une erreur inattendue s'est produite: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred: " + ex.getMessage());
    }
}