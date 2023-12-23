package com.pja.bloodcount.controller;


import com.pja.bloodcount.model.BloodCountReference;
import com.pja.bloodcount.service.contract.BCReferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bc-ref")
@RequiredArgsConstructor
@Slf4j
public class BCReferenceController {

    private final BCReferenceService service;

    @PostMapping("/populate")
    public ResponseEntity<List<BloodCountReference>> populate() {
        return new ResponseEntity<>(service.populateTable(), HttpStatus.OK);
    }

    @GetMapping("/table")
    public ResponseEntity<List<BloodCountReference>> pageQuery() {
        List<BloodCountReference> table = service.fullTableOfBCReference();
        return new ResponseEntity<>(table, HttpStatus.OK);
    }
}
