package com.pja.bloodcount.exceptions.advicor;

import com.pja.bloodcount.exceptions.ResourceConflictException;
import com.pja.bloodcount.exceptions.UserNotFoundException;
import com.pja.bloodcount.exceptions.UserWithEmailNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ControllerAdvice class to handle all exceptions related to UserController
 * handle exceptions:
 * ResourceConflictException
 * UserNotFoundException
 * UserWithEmailNotFoundException
 */
@ControllerAdvice
public class UserControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<Object> handleResourceConflictException(
            ResourceConflictException ex, WebRequest request) {

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("timestamp", LocalDateTime.now());
        payload.put("message", "CONFLICT! - User with " + ex.getEmail() + " already exists");
        return new ResponseEntity<>(payload, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(
            UserNotFoundException ex, WebRequest request) {

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("timestamp", LocalDateTime.now());
        payload.put("message", "DOEST EXIST! - User with " + ex.getId() + " not found");
        return new ResponseEntity<>(payload, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserWithEmailNotFoundException.class)
    public ResponseEntity<Object> handleUserWithEmailNotFoundException(
            UserWithEmailNotFoundException ex, WebRequest request) {

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("timestamp", LocalDateTime.now());
        payload.put("message", "DOEST EXIST! - User with " + ex.getEmail() + " not found");
        return new ResponseEntity<>(payload, HttpStatus.NOT_FOUND);
    }
}
