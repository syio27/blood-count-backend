package com.pja.bloodcount.exceptions.handler;

import com.pja.bloodcount.exceptions.LanguageNotSupportedException;
import com.pja.bloodcount.exceptions.QuestionNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class LanguageExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(LanguageNotSupportedException.class)
    public ResponseEntity<Object> handleLanguageNotSupportedException(
            LanguageNotSupportedException ex, WebRequest request) {

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("timestamp", LocalDateTime.now());
        payload.put("message", ex.getMessage());
        return new ResponseEntity<>(payload, HttpStatus.METHOD_NOT_ALLOWED);
    }
}
