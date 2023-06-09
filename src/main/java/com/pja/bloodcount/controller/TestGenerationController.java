package com.pja.bloodcount.controller;

import com.pja.bloodcount.model.Patient;
import com.pja.bloodcount.service.GenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/generation")
@RequiredArgsConstructor
@Slf4j
public class TestGenerationController {

    private final GenerationService service;

    @GetMapping("/{caseId}/patient")
    public ResponseEntity<Patient> generatePatient(@PathVariable Long caseId){
        return ResponseEntity.ok(service.generatePatient(caseId));
    }
}
