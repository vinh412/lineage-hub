package com.lineagehub.exception;

import com.lineagehub.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, 
                                                                 HttpServletRequest request) {
        log.error("Resource not found: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, 
                                                                  HttpServletRequest request) {
        log.error("Business exception: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.CONFLICT.value())
                .error("Business Rule Violation")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex, 
                                                            HttpServletRequest request) {
        log.error("Unauthorized: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error("Forbidden")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(DuplicateResourceException ex, 
                                                                  HttpServletRequest request) {
        log.error("Duplicate resource: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.CONFLICT.value())
                .error("Conflict")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex, 
                                                              HttpServletRequest request) {
        log.error("Bad credentials: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Unauthorized")
                .message("Email hoặc mật khẩu không chính xác")
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, 
                                                            HttpServletRequest request) {
        log.error("Access denied: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error("Forbidden")
                .message("Bạn không có quyền truy cập tài nguyên này")
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, 
                                                                     HttpServletRequest request) {
        log.error("Validation error: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        String message = errors.values().stream()
                .collect(Collectors.joining(", "));
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Error")
                .message(message)
                .path(request.getRequestURI())
                .details(errors)
                .build();
        
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, 
                                                                 HttpServletRequest request) {
        log.error("Unexpected error", ex);
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("Đã xảy ra lỗi không mong muốn. Vui lòng thử lại sau.")
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
