package com.pja.bloodcount.service.contract;

import com.pja.bloodcount.dto.request.CreateAbnormalityRequest;
import com.pja.bloodcount.dto.request.CreateCaseRequest;
import com.pja.bloodcount.dto.response.CaseResponse;
import com.pja.bloodcount.model.Case;

import java.util.List;

public interface CaseService {
    CaseResponse createCase(CreateCaseRequest request);
    void createBCAbnormality(Long caseId, CreateAbnormalityRequest request);
    CaseResponse getCaseWithAbnormalities(Long id);
    List<CaseResponse> getAllCasesWithAbnormalities();
    void deleteCase(Long id);
}
