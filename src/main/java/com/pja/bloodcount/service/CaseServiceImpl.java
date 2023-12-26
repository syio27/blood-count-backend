package com.pja.bloodcount.service;

import com.pja.bloodcount.dto.request.CreateAbnormalityRequest;
import com.pja.bloodcount.dto.request.CreateCaseRequest;
import com.pja.bloodcount.dto.response.CaseResponse;
import com.pja.bloodcount.exceptions.CaseNotFoundException;
import com.pja.bloodcount.mapper.AbnormalityMapper;
import com.pja.bloodcount.mapper.CaseMapper;
import com.pja.bloodcount.model.BloodCountAbnormality;
import com.pja.bloodcount.model.Case;
import com.pja.bloodcount.repository.CaseRepository;
import com.pja.bloodcount.service.contract.CaseService;
import com.pja.bloodcount.utils.RangeValidationUtils;
import com.pja.bloodcount.validation.CaseValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CaseServiceImpl implements CaseService {

    private final CaseRepository repository;
    private final CaseValidator validator;

    @Override
    public CaseResponse createCase(CreateCaseRequest request) {
        if (isSecondRangeAdded(request)) {
            RangeValidationUtils.validateRanges(
                    request.getFirstMinAge(),
                    request.getFirstMaxAge(),
                    request.getSecondMinAge(),
                    request.getSecondMaxAge());
        } else {
            RangeValidationUtils.validateRanges(
                    request.getFirstMinAge(),
                    request.getFirstMaxAge());
        }

        if (request.getLanguage() == null) {
            throw new IllegalArgumentException("Language cannot be null");
        }

        if (request.getHr().isBlank() || request.getDiagnosis().isBlank() || request.getAnemiaType().isBlank()
                || request.getRr().isBlank() || request.getInfoCom().isBlank() || request.getHeight().isBlank()
                || request.getCaseName().isBlank() || request.getBmi().isBlank() || request.getBodyMass().isBlank()
                || request.getDescription().isBlank()) {
            throw new IllegalArgumentException("Cannot be blank string");
        }

        Case newCase = CaseMapper.mapRequestToEntity(request);
        return CaseMapper.mapToResponseDTO(repository.save(newCase));
    }

    private static boolean isSecondRangeAdded(CreateCaseRequest request) {
        return request.getSecondMinAge() != 0 && request.getSecondMaxAge() != 0;
    }

    @Override
    public void createBCAbnormality(Long caseId, List<CreateAbnormalityRequest> createAbnormalityRequestList) {
        Case aCase = validator.validateIfExistsAndGet(caseId);
        List<BloodCountAbnormality> abnormalities = createAbnormalityRequestList.stream()
                .map(AbnormalityMapper::mapToBloodCountAbnormality)
                .toList();
        aCase.addAllAbnormalities(abnormalities);
        repository.save(aCase);
    }

    @Override
    public CaseResponse getCaseWithAbnormalities(Long id) {
        return repository.findCaseWithAbnormalities(id)
                .map(CaseMapper::mapToResponseDTO)
                .orElseThrow(() -> new CaseNotFoundException(id));
    }

    @Override
    public List<CaseResponse> getAllCasesWithAbnormalities() {
        return repository.findAllCasesWithAbnormalities()
                .stream()
                .distinct()
                .map(CaseMapper::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCase(Long id) {
        Case aCase = validator.validateIfExistsAndGet(id);
        repository.delete(aCase);
    }
}