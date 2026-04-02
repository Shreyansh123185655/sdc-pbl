package com.studentpdf.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Central error handler — converts exceptions to structured JSON responses.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // ── Bean Validation errors ────────────────────────────────────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        return buildError(HttpStatus.UNPROCESSABLE_ENTITY, "Validation failed", errors);
    }

    // ── PDF generation errors ─────────────────────────────────────────────────
    @ExceptionHandler(PdfGenerationException.class)
    public ResponseEntity<Map<String, Object>> handlePdfGen(PdfGenerationException ex) {
        log.error("PDF generation error for [{}]: {}", ex.getEnrollmentNo(), ex.getMessage(), ex);
        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "PDF generation failed for enrollment: " + ex.getEnrollmentNo(),
                List.of(ex.getMessage())
        );
    }

    // ── Generic fallback ──────────────────────────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                List.of(ex.getMessage())
        );
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private ResponseEntity<Map<String, Object>> buildError(
            HttpStatus status, String message, List<String> errors) {

        Map<String, Object> body = new HashMap<>();
        body.put("status",    status.value());
        body.put("message",   message);
        body.put("errors",    errors);
        body.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.status(status).body(body);
    }
}
