package com.pja.bloodcount.exceptions.handler;

import com.pja.bloodcount.exceptions.*;
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
public class UserExceptionsHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserConflictException.class)
    public ResponseEntity<Object> handleResourceConflictException(
            UserConflictException ex, WebRequest request) {

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("timestamp", LocalDateTime.now());
        payload.put("message", "User with " + ex.getEmail() + " already exists");
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

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Object> handleInvalidCredentialsException(
            InvalidCredentialsException ex, WebRequest request) {

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("timestamp", LocalDateTime.now());
        payload.put("message", ex.getMessage());
        return new ResponseEntity<>(payload, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(PasswordValidationException.class)
    public ResponseEntity<Object> handlePasswordValidationException(
            PasswordValidationException ex, WebRequest request) {

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("timestamp", LocalDateTime.now());
        payload.put("message", ex.getMessage());
        return new ResponseEntity<>(payload, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PasswordSameAsOldException.class)
    public ResponseEntity<Object> handlePasswordSameAsOldException(
            PasswordSameAsOldException ex, WebRequest request) {

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("timestamp", LocalDateTime.now());
        payload.put("message", ex.getMessage());
        return new ResponseEntity<>(payload, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PasswordRepeatException.class)
    public ResponseEntity<Object> handlePasswordRepeatException(
            PasswordRepeatException ex, WebRequest request) {

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("timestamp", LocalDateTime.now());
        payload.put("message", ex.getMessage());
        return new ResponseEntity<>(payload, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IncorrectOldPasswordException.class)
    public ResponseEntity<Object> handleOldPasswordNotCorrectException(
            IncorrectOldPasswordException ex, WebRequest request) {

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("timestamp", LocalDateTime.now());
        payload.put("message", ex.getMessage());
        return new ResponseEntity<>(payload, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailValidationException.class)
    public ResponseEntity<Object> handleEmailValidationException(
            EmailValidationException ex, WebRequest request) {

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("timestamp", LocalDateTime.now());
        payload.put("message", ex.getMessage());
        return new ResponseEntity<>(payload, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RoleAccessException.class)
    public ResponseEntity<Object> handleRoleAccessException(
            RoleAccessException ex, WebRequest request) {

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("timestamp", LocalDateTime.now());
        payload.put("message", ex.getMessage());
        return new ResponseEntity<>(payload, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserNotAllowedException.class)
    public ResponseEntity<Object> handleUserNotAllowedException(
            UserNotAllowedException ex, WebRequest request){

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("timestamp", LocalDateTime.now());
        payload.put("message", ex.getMessage());
        return new ResponseEntity<>(payload, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserBannedException.class)
    public ResponseEntity<Object> handleUserBannedException(
            UserBannedException ex, WebRequest request){

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("timestamp", LocalDateTime.now());
        payload.put("message", ex.getMessage());
        return new ResponseEntity<>(payload, HttpStatus.NOT_ACCEPTABLE);
    }
}
