package com.develop.management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Entity not found", List.of(ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, "Access is denied", List.of(ex.getMessage()));
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, "Access is denied", List.of(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", List.of(ex.getMessage()));
    }

    @ExceptionHandler(InvalidSpaceshipIdException.class)
    public ResponseEntity<ErrorResponse> handleInvalidSpaceshipIdException(InvalidSpaceshipIdException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid spaceship ID", List.of(ex.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Unauthorized: ", List.of(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Request body is not valid", List.of(ex.getMessage()));
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, List<String> errors) {
        ErrorResponse response = ErrorResponse.builder()
                .status(status.value())
                .message(message)
                .errors(errors != null ? errors : Collections.emptyList())
                .build();
        return ResponseEntity.status(status).body(response);
    }
}
