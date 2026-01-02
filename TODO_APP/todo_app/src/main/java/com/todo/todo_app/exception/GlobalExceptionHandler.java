package com.todo.todo_app.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<ApiError> handleNotFound(NotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI(), List.of());
    }

    @ExceptionHandler(ConflictException.class)
    ResponseEntity<ApiError> handleConflict(ConflictException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), req.getRequestURI(), List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<ApiError.FieldViolation> violations = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .filter(err -> err instanceof FieldError)
                .map(err -> (FieldError) err)
                .map(fe -> new ApiError.FieldViolation(fe.getField(), fe.getDefaultMessage()))
                .toList();

        return build(HttpStatus.BAD_REQUEST, "Validation failed", req.getRequestURI(), violations);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, "Data integrity violation", req.getRequestURI(), List.of());
    }

//    @ExceptionHandler(Exception.class)
//    ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req) {
//        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", req.getRequestURI(), List.of());
//    }

    private ResponseEntity<ApiError> build(HttpStatus status, String message, String path, List<ApiError.FieldViolation> violations) {
        ApiError body = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                violations
        );
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req) {
        ex.printStackTrace(); // jednoduché, v praxi logger
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getClass().getSimpleName() + ": " + ex.getMessage(), req.getRequestURI(), List.of());
    }

}