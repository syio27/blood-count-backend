package com.pja.bloodcount.service;

import com.pja.bloodcount.dto.response.PatientResponse;
import com.pja.bloodcount.mapper.PatientMapper;
import com.pja.bloodcount.model.Patient;
import com.pja.bloodcount.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository repository;

    public PatientResponse getPatientWithBloodCounts(Long patientId){
        return PatientMapper.mapToResponseDTO(repository.findById(patientId).get());
    }

    public List<PatientResponse> getAllPatientsWithBloodCounts(){
        List<Patient> patients = repository.findAll()
                .stream()
                .distinct()
                .collect(Collectors.toList());
        return PatientMapper.mapToResponseListDTO(patients);
    }
}
