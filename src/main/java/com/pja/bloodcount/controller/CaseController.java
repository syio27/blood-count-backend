package com.pja.bloodcount.controller;

import com.pja.bloodcount.dto.request.CreateAbnormalityRequest;
import com.pja.bloodcount.dto.request.CreateCaseRequest;
import com.pja.bloodcount.dto.response.CaseResponse;
import com.pja.bloodcount.model.Case;
import com.pja.bloodcount.service.contract.CaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cases")
@RequiredArgsConstructor
@Slf4j
public class CaseController {

    private final CaseService service;

    @PreAuthorize("hasRole('ROOT') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CaseResponse> createCase(@RequestBody CreateCaseRequest request){
        return ResponseEntity.ok(service.createCase(request));
    }

    @GetMapping
    public ResponseEntity<List<CaseResponse>> getAllCases(){
        return ResponseEntity.ok(service.getAllCases());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CaseResponse> getCase(@PathVariable Long id){
        return ResponseEntity.ok(service.getCaseById(id));
    }

    @PreAuthorize("hasRole('ROOT') or hasRole('ADMIN')")
    @PostMapping("/{caseId}/abnormality")
    public ResponseEntity<Void> createAbnormality(@PathVariable Long caseId, @RequestBody CreateAbnormalityRequest request){
        service.createBCAbnormality(caseId, request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/abnormalities")
    public ResponseEntity<CaseResponse> getCaseWithAbnormalities(@PathVariable Long id){
        return ResponseEntity.ok(service.getCaseWithAbnormalities(id));
    }

    @GetMapping("/abnormalities")
    public ResponseEntity<List<CaseResponse>> getAllCasesWithAbnormalities(){
        return ResponseEntity.ok(service.getAllCasesWithAbnormalities());
    }

    @PreAuthorize("hasRole('ROOT') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCase(@PathVariable Long id){
        service.deleteCase(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
