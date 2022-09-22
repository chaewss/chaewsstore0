package com.chaewsscode.chaewsstore.exception;

import com.chaewsscode.chaewsstore.util.ResponseCode;
import com.chaewsscode.chaewsstore.util.ResponseData;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ForbiddenException.class)
    protected ResponseEntity<ResponseData> handleForbiddenException(ForbiddenException e) {
        logger.info("{} ({}) :: {}", e.getResponseCode(), e.getResponseCode().getHttpStatus().toString(), e.getResponseCode().getDetail());
        return ResponseData.toResponseEntity(e.getResponseCode());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<ResponseData> handleResourceNotFoundException(ResourceNotFoundException e) {
        logger.info("{} ({}) :: {}", e.getResponseCode(), e.getResponseCode().getHttpStatus().toString(), e.getResponseCode().getDetail());
        return ResponseData.toResponseEntity(e.getResponseCode());
    }

    @ExceptionHandler(DuplicateException.class)
    protected ResponseEntity<ResponseData> handleDuplicateException(DuplicateException e) {
        logger.info("{} ({}) :: {}", e.getResponseCode(), e.getResponseCode().getHttpStatus().toString(), e.getResponseCode().getDetail());
        return ResponseData.toResponseEntity(e.getResponseCode());
    }

    @ExceptionHandler(UnknownAuthenticationException.class)
    protected String handleUnknownAuthenticationException(
        UnknownAuthenticationException e) {
        log.error("handleUnknownAuthenticationException : {}", e.getMessage());
        return e.getMessage();
    }

    @ExceptionHandler(MalformedJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected ResponseEntity<ResponseData> handleMalformedJwtException(
            MalformedJwtException e) {
        log.error("handleMalformedJwtException : {}", e.getMessage());
        return ResponseData.toResponseEntity(ResponseCode.INVALID_AUTH_TOKEN);
    }

    @ExceptionHandler(io.jsonwebtoken.security.SecurityException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected ResponseEntity<ResponseData> handleJwtSecurityException(
            io.jsonwebtoken.security.SecurityException e) {
        log.error("handleJwtSecurityException : {}", e.getMessage());
        return ResponseData.toResponseEntity(ResponseCode.INVALID_AUTH_TOKEN);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected ResponseEntity<ResponseData> handleExpiredJwtException(
            ExpiredJwtException e) {
        log.error("handleExpiredJwtException : {}", e.getMessage());
        return ResponseData.toResponseEntity(ResponseCode.INVALID_AUTH_TOKEN);
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected ResponseEntity<ResponseData> handleUnsupportedJwtException(UnsupportedJwtException e) {
        log.error("handleUnsupportedJwtException : {}", e.getMessage());
        return ResponseData.toResponseEntity(ResponseCode.INVALID_AUTH_TOKEN);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity handleConstraintViolationException(ConstraintViolationException e) {
        logger.error("ERROR :: ConstraintViolationException - {}({})", e.getMessage(), e.getClass().getName());
        Map<String, String> data = new HashMap<>();
        e.getConstraintViolations().stream().forEach(cv -> data.put(cv.getPropertyPath().toString(), cv.getMessage()));
        return ResponseData.toResponseEntity(ResponseCode.VALID_ERROR, data);
    }

    @Override
    protected ResponseEntity handleBindException(BindException ex, HttpHeaders headers,
        HttpStatus status, WebRequest request) {
        logger.error("ERROR :: BindException - {}({})", ex.getMessage(), ex.getClass().getName());
        Map<String, String> data = new HashMap<>();
        ex.getBindingResult().getFieldErrors().stream().forEach(e -> data.put(e.getField(), e.getDefaultMessage()));
        return ResponseData.toResponseEntity(ResponseCode.VALID_ERROR, data);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String exception(Exception e) {
        logger.error("ERROR :: Internal Server Exception - {}({})", e.getMessage(), e.getClass().getName());
        return e.getMessage();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(IllegalArgumentException e) {
        logger.error("ERROR :: IllegalArgumentException - {}({})", e.getMessage(), e.getClass().getName());
        return e.getMessage();
    }

    @Override
    protected ResponseEntity handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
        HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<FieldError> allFieldErrors = ex.getBindingResult().getFieldErrors();
        Map<String, String> data = new HashMap<>();
        for (FieldError fieldError : allFieldErrors) {
            data.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseData.toResponseEntity(ResponseCode.VALID_ERROR, data);
    }
}
