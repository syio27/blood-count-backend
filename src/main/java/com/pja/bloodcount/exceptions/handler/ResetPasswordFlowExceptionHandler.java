package com.pja.bloodcount.exceptions.handler;

import com.pja.bloodcount.exceptions.ReferenceTableException;
import com.pja.bloodcount.exceptions.ResetTokenInvalidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class ResetPasswordFlowExceptionHandler {

    @ExceptionHandler(ResetTokenInvalidException.class)
    public ResponseEntity<Object> handleResetTokenInvalidException(
            ResetTokenInvalidException ex, WebRequest request) {

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("timestamp", LocalDateTime.now());
        payload.put("message", ex.getMessage());
        return new ResponseEntity<>(payload, HttpStatus.UNAUTHORIZED);
    }
}
