package com.pja.bloodcount.controller;

import com.pja.bloodcount.dto.response.PatientResponse;
import com.pja.bloodcount.model.Patient;
import com.pja.bloodcount.service.GenerationService;
import com.pja.bloodcount.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/generation")
@RequiredArgsConstructor
@Slf4j
public class TestGenerationController {

    private final GenerationService service;
    private final PatientService patientService;

    @GetMapping("/{caseId}/patient")
    public ResponseEntity<Patient> generatePatient(@PathVariable Long caseId){
        return ResponseEntity.ok(service.generatePatient(caseId));
    }

    @GetMapping("/{caseId}/{patientId}/bc")
    public ResponseEntity<Void> generateBloodCount(@PathVariable Long caseId,
                                                            @PathVariable Long patientId){
        service.generateBloodCount(caseId, patientId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/patients/{patientId}")
    public ResponseEntity<PatientResponse> getPatient(@PathVariable Long patientId){
        return ResponseEntity.ok(patientService.getPatientWithBloodCounts(patientId));
    }

    @GetMapping("/patients")
    public ResponseEntity<List<PatientResponse>> getAllPatients(){
        return ResponseEntity.ok(patientService.getAllPatientsWithBloodCounts());
    }
}
