package com.leavemanagement.leave_api.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

// Spring Boot's default /error page drops exception messages by default
// (server.error.include-message defaults to never), so every 4xx in the API
// came back with no indication of what was wrong. Rather than flipping that
// property globally - which would also expose the raw message of any
// unexpected exception, not just the ones we've deliberately made
// client-safe - this handles the specific, vetted exception types by name.
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "conflict", "message", "The request conflicts with an existing record"));
    }

    // Every ResponseStatusException thrown across the app already carries a
    // deliberately client-safe reason string - this just stops it being
    // discarded before the response is written.
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatusException(ResponseStatusException e) {
        HttpStatus status = HttpStatus.resolve(e.getStatusCode().value());
        String errorCode = status != null ? status.name().toLowerCase() : "error";
        return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", errorCode, "message", String.valueOf(e.getReason())));
    }

    // Thrown by @Valid on a @RequestBody - field-level messages from
    // annotations like @NotBlank, joined into one readable string.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(Map.of("error", "validation_failed", "message", message));
    }

    // Thrown for malformed JSON, or a value that fails to deserialize into
    // its target type (most commonly an unrecognized enum constant, e.g.
    // {"stage": "NOT_A_STAGE"} or an interview_expertise bracket key that
    // isn't a real ExperienceBracket) - found live while testing Batch 2's
    // interview_expertise validation, but it applies to every enum-typed
    // request field across the app, not just this batch's. Without this
    // handler it fell through to Spring Boot's default /error body with no
    // message, the same problem this class exists to solve for every other
    // exception type here.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleMalformedRequestBody(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(Map.of("error", "validation_failed", "message", "Malformed request body"));
    }

    // Thrown when a @RequestParam/@PathVariable fails to convert to its
    // target type - most commonly an unrecognized enum constant on a query
    // param (e.g. ?status=BOGUS against an enum-typed status filter).
    // Without this handler it fell through to Spring's default
    // binding-failure body, a different (and less informative) shape than
    // every other 400 in this API - found live while testing
    // InterviewerRosterStatus/AccountStatus-typed query params, but it
    // applies to every enum-typed @RequestParam across the app.
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        String expected = e.getRequiredType() != null && e.getRequiredType().isEnum()
                ? " - expected one of " + Arrays.toString(e.getRequiredType().getEnumConstants())
                : "";
        String message = e.getName() + ": invalid value '" + e.getValue() + "'" + expected;
        return ResponseEntity.badRequest().body(Map.of("error", "validation_failed", "message", message));
    }

    // Thrown by Spring's multipart resolver once a request exceeds
    // spring.servlet.multipart.max-file-size/max-request-size, before the
    // request even reaches a controller - without this handler it falls
    // through to Spring Boot's default /error body with no message
    // (server.error.include-message defaults to never), the same problem
    // this class exists to solve for every other exception type here.
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e) {
        return ResponseEntity.status(HttpStatus.CONTENT_TOO_LARGE)
                .body(Map.of("error", "content_too_large", "message", "The uploaded file is too large"));
    }
}

