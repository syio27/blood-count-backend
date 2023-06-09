package com.pja.bloodcount.controller;

import com.pja.bloodcount.dto.response.UserResponse;
import com.pja.bloodcount.model.BloodCountReference;
import com.pja.bloodcount.service.contract.BCReferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bc-ref")
@RequiredArgsConstructor
@Slf4j
public class BCReferenceController {

    private final BCReferenceService service;

    @GetMapping("/table")
    public ResponseEntity<List<BloodCountReference>> pageQuery(){
        List<BloodCountReference> table = service.fullTableOfBCReference();
        return new ResponseEntity<>(table, HttpStatus.OK);
    }
}
