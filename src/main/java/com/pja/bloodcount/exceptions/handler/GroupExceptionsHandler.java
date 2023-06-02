package com.pja.bloodcount.exceptions.handler;

import com.pja.bloodcount.exceptions.GroupConflictException;
import com.pja.bloodcount.exceptions.GroupNotFoundException;
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
public class GroupExceptionsHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(GroupNotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(
            GroupNotFoundException ex, WebRequest request) {

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("timestamp", LocalDateTime.now());
        payload.put("message", "Group with group number" + ex.getGroupNumber() + " didnt found");
        return new ResponseEntity<>(payload, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(GroupConflictException.class)
    public ResponseEntity<Object> handleNotFoundException(
            GroupConflictException ex, WebRequest request) {

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("timestamp", LocalDateTime.now());
        payload.put("message", "Group with group number" + ex.getGroupNumber() + " already exists");
        return new ResponseEntity<>(payload, HttpStatus.CONFLICT);
    }
}
