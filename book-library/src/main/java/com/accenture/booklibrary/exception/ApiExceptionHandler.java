package com.accenture.booklibrary.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler({Exception.class, NoSuchMethodException.class})
    public ResponseEntity<ApiError> handleException(Exception e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String errorMessage = status.name();
        if (e instanceof NoSuchElementException) {
            status = HttpStatus.BAD_REQUEST;
            errorMessage = e.getMessage();
        }
        if (e instanceof AuthorizationDeniedException) {
           status = HttpStatus.FORBIDDEN;
           errorMessage = e.getMessage();
        }

        ApiError apiError = new ApiError(errorMessage, status.value(), LocalDateTime.now(), request.getRequestURI());
        return new ResponseEntity<>(apiError, status);
    }
}
