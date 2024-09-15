package com.pja.bloodcount.service.contract;

import com.pja.bloodcount.model.Patient;

public interface GenerationService {

    Patient generatePatient(Long caseId);
    void generateBloodCount(Long caseId, Long patientId);
}
