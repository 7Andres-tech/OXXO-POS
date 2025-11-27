package com.oxxo.error;


import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;
import java.util.*;


@RestControllerAdvice
public class GlobalExceptionHandler {


@ExceptionHandler(NotFoundException.class)
public ResponseEntity<?> notFound(NotFoundException ex, WebRequest req) {
return build(HttpStatus.NOT_FOUND, ex.getMessage(), req.getDescription(false));
}


@ExceptionHandler(BusinessException.class)
public ResponseEntity<?> business(BusinessException ex, WebRequest req) {
return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), req.getDescription(false));
}


@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<?> invalid(MethodArgumentNotValidException ex, WebRequest req) {
Map<String, Object> body = base(HttpStatus.BAD_REQUEST, "Validación fallida", req.getDescription(false));
Map<String, String> fields = new LinkedHashMap<>();
ex.getBindingResult().getFieldErrors().forEach(err -> fields.put(err.getField(), err.getDefaultMessage()));
body.put("fields", fields);
return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
}


@ExceptionHandler(Exception.class)
public ResponseEntity<?> generic(Exception ex, WebRequest req) {
return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), req.getDescription(false));
}


private ResponseEntity<Map<String, Object>> build(HttpStatus status, String message, String path) {
return ResponseEntity.status(status).body(base(status, message, path));
}


private Map<String, Object> base(HttpStatus status, String message, String path) {
Map<String, Object> body = new LinkedHashMap<>();
body.put("timestamp", LocalDateTime.now().toString());
body.put("status", status.value());
body.put("error", status.getReasonPhrase());
body.put("message", message);
body.put("path", path);
return body;
}
}