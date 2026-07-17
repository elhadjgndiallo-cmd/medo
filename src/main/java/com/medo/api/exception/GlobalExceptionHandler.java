package com.medo.api.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, WebRequest req) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(e ->
            errors.put(((FieldError) e).getField(), e.getDefaultMessage()));
        ApiError err = new ApiError(LocalDateTime.now(), 400, "Validation échouée",
            "Champs invalides", path(req), errors);
        return ResponseEntity.badRequest().body(err);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuth(AuthenticationException ex, WebRequest req) {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), req);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccess(AccessDeniedException ex, WebRequest req) {
        return build(HttpStatus.FORBIDDEN, "Accès refusé", req);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, WebRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError> handleDuplicate(DuplicateResourceException ex, WebRequest req) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), req);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiError> handleBusiness(BusinessRuleException ex, WebRequest req) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAll(Exception ex, WebRequest req) {
        log.error("Erreur inattendue : {}", ex.getMessage(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur interne. Réessayez.", req);
    }

    private ResponseEntity<ApiError> build(HttpStatus s, String msg, WebRequest req) {
        return ResponseEntity.status(s).body(
            new ApiError(LocalDateTime.now(), s.value(), s.getReasonPhrase(), msg, path(req), null));
    }

    private String path(WebRequest req) {
        String d = req.getDescription(false);
        return d.startsWith("uri=") ? d.substring(4) : d;
    }

    // ── DTO erreur ──
    public static class ApiError {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        private Map<String, String> fieldErrors;

        public ApiError() {}
        public ApiError(LocalDateTime timestamp, int status, String error,
                        String message, String path, Map<String, String> fieldErrors) {
            this.timestamp = timestamp; this.status = status;
            this.error = error; this.message = message;
            this.path = path; this.fieldErrors = fieldErrors;
        }
        public LocalDateTime getTimestamp() { return timestamp; }
        public int getStatus()              { return status; }
        public String getError()            { return error; }
        public String getMessage()          { return message; }
        public String getPath()             { return path; }
        public Map<String, String> getFieldErrors() { return fieldErrors; }
    }

    // ── Exceptions métier ──
    public static class AuthenticationException extends RuntimeException {
        public AuthenticationException(String m) { super(m); } }

    public static class TenantAccessException extends RuntimeException {
        public TenantAccessException(String m) { super(m); } }

    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String m) { super(m); }
        public ResourceNotFoundException(String r, String id) { super(r + " introuvable : " + id); } }

    public static class DuplicateResourceException extends RuntimeException {
        public DuplicateResourceException(String m) { super(m); } }

    public static class BusinessRuleException extends RuntimeException {
        public BusinessRuleException(String m) { super(m); } }

    public static class StockInsuffisantException extends BusinessRuleException {
        public StockInsuffisantException(String p, int d, int s) {
            super("Stock insuffisant '" + p + "' : demandé=" + d + ", dispo=" + s); } }

    public static class SessionCaisseException extends BusinessRuleException {
        public SessionCaisseException(String m) { super(m); } }

    public static class PermissionDeniedException extends RuntimeException {
        public PermissionDeniedException(String mod, String act) {
            super("Permission refusée : " + mod + "." + act); } }
}
