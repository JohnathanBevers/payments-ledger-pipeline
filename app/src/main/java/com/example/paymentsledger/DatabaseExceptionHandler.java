package com.example.paymentsledger;

import jakarta.persistence.PersistenceException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DatabaseExceptionHandler {

    @ExceptionHandler({DataAccessException.class, PersistenceException.class})
    public ResponseEntity<ErrorResponse> handleDatabaseExceptions(Exception exception) {
        ErrorResponse response = new ErrorResponse(
                "Database service temporarily unavailable. Please try again later.",
                exception.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    public static class ErrorResponse {
        private final String message;
        private final String detail;

        public ErrorResponse(String message, String detail) {
            this.message = message;
            this.detail = detail;
        }

        public String getMessage() {
            return message;
        }

        public String getDetail() {
            return detail;
        }
    }
}
