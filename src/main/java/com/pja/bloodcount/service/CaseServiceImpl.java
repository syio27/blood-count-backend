package com.pja.bloodcount.service;

import com.pja.bloodcount.dto.request.CreateAbnormalityRequest;
import com.pja.bloodcount.dto.request.CreateCaseRequest;
import com.pja.bloodcount.dto.response.AbnormalityResponse;
import com.pja.bloodcount.dto.response.CaseResponse;
import com.pja.bloodcount.exceptions.CaseNotFoundException;
import com.pja.bloodcount.exceptions.RangeArgumentException;
import com.pja.bloodcount.mapper.CaseMapper;
import com.pja.bloodcount.model.BloodCountAbnormality;
import com.pja.bloodcount.model.Case;
import com.pja.bloodcount.repository.CaseRepository;
import com.pja.bloodcount.service.contract.CaseService;
import com.pja.bloodcount.validation.CaseValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CaseServiceImpl implements CaseService {

    private final CaseRepository repository;
    private final CaseValidator validator;

    @Override
    public CaseResponse createCase(CreateCaseRequest request) {

        if(request.getSecondMinAge() != 0 && request.getSecondMaxAge() != 0){
            validateRanges(
                    request.getFirstMinAge(),
                    request.getFirstMaxAge(),
                    request.getSecondMinAge(),
                    request.getSecondMaxAge());
        }

        validateRanges(
                request.getFirstMinAge(),
                request.getFirstMaxAge());

       Case newCase = Case
               .builder()
               .firstMinAge(request.getFirstMinAge())
               .firstMaxAge(request.getFirstMaxAge())
               .secondMinAge(request.getSecondMinAge())
               .secondMaxAge(request.getSecondMaxAge())
               .anemiaType(request.getAnemiaType())
               .affectedGender(request.getAffectedGender())
               .diagnosis(request.getDiagnosis())
               .build();
       return CaseMapper.mapToResponseDTO(repository.save(newCase));
    }

    @Override
    public void createBCAbnormality(Long caseId, List<CreateAbnormalityRequest> createAbnormalityRequestList) {
        Case aCase = validator.validateIfExistsAndGet(caseId);

        log.info("request size: {}", createAbnormalityRequestList.size());
        for(CreateAbnormalityRequest abnormalityRequest : createAbnormalityRequestList){
            BloodCountAbnormality abnormality = BloodCountAbnormality
                    .builder()
                    .parameter(abnormalityRequest.getParameter())
                    .minValue(abnormalityRequest.getMinValue())
                    .maxValue(abnormalityRequest.getMaxValue())
                    .type(abnormalityRequest.getType())
                    .build();

            aCase.addAbnormality(abnormality);
        }

        repository.save(aCase);
    }

    @Override
    public CaseResponse getCaseWithAbnormalities(Long id) {
        Optional<Case> aCase = repository.findCaseWithAbnormalities(id);
        if(aCase.isEmpty()){
            throw new CaseNotFoundException(id);
        }
        return CaseMapper.mapToResponseDTO(aCase.get());
    }

    @Override
    public List<CaseResponse> getAllCasesWithAbnormalities() {
        List<Case> cases = repository.findAllCasesWithAbnormalities()
                .stream()
                .distinct()
                .collect(Collectors.toList());
        log.info("Size of all cases: {}", cases.size());
        return CaseMapper.mapToResponseListDTO(cases);
    }

    @Override
    public void deleteCase(Long id) {
        Case aCase = validator.validateIfExistsAndGet(id);
        repository.delete(aCase);
    }

    private void validateRanges(int firstMinAge, int firstMaxAge, int secondMinAge, int secondMaxAge) throws RangeArgumentException{
        if(firstMinAge < 18 || firstMinAge >= firstMaxAge){
            throw new RangeArgumentException("Bad min age");
        }
        if(firstMaxAge > 75){
            throw new RangeArgumentException("Bad max age");
        }
        if(secondMinAge < 18 || secondMinAge >= secondMaxAge){
            throw new RangeArgumentException("Bad min age");
        }
        if(secondMaxAge > 75){
            throw new RangeArgumentException("Bad max age");
        }
    }

    private void validateRanges(int firstMinAge, int firstMaxAge) throws RangeArgumentException{
        if(firstMinAge < 18 || firstMinAge >= firstMaxAge){
            throw new RangeArgumentException("Bad min age");
        }
        if(firstMaxAge > 75){
            throw new RangeArgumentException("Bad max age");
        }
    }
}