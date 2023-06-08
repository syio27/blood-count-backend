package com.pja.bloodcount.exceptions.handler;

import com.pja.bloodcount.exceptions.CaseNotFoundException;
import com.pja.bloodcount.exceptions.RangeArgumentException;
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
public class CaseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RangeArgumentException.class)
    public ResponseEntity<Object> handleRangeArgumentException(
            RangeArgumentException ex, WebRequest request) {

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("timestamp", LocalDateTime.now());
        payload.put("message", ex.getMessage());
        return new ResponseEntity<>(payload, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CaseNotFoundException.class)
    public ResponseEntity<Object> handleCaseNotFoundException(
            CaseNotFoundException ex, WebRequest request) {

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("timestamp", LocalDateTime.now());
        payload.put("message", "Case with id: " + ex.getId() + " doest exist");
        return new ResponseEntity<>(payload, HttpStatus.NOT_FOUND);
    }
}
