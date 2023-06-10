package com.pja.bloodcount.exceptions.handler;

import com.pja.bloodcount.exceptions.GenderGenerationException;
import com.pja.bloodcount.exceptions.PatientBloodCountConflictException;
import com.pja.bloodcount.exceptions.PatientNotFoundException;
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
public class GenerationExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(GenderGenerationException.class)
    public ResponseEntity<Object> handleGenderGenerationException(
            GenderGenerationException ex, WebRequest request) {

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("timestamp", LocalDateTime.now());
        payload.put("message", ex.getMessage());
        return new ResponseEntity<>(payload, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<Object> handlePatientNotFoundException(
            PatientNotFoundException ex, WebRequest request) {

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("timestamp", LocalDateTime.now());
        payload.put("message", "Patient with id: " + ex.getId() + " doesnt exist");
        return new ResponseEntity<>(payload, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PatientBloodCountConflictException.class)
    public ResponseEntity<Object> handlePatientBloodCountConflictException(
            PatientBloodCountConflictException ex, WebRequest request) {

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("timestamp", LocalDateTime.now());
        payload.put("message", "Patient with id: " + ex.getId() + " already has blood count");
        return new ResponseEntity<>(payload, HttpStatus.CONFLICT);
    }
}
