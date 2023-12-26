package com.pja.bloodcount.mapper;

import com.pja.bloodcount.dto.request.CreateCaseRequest;
import com.pja.bloodcount.dto.response.CaseOfGameResponse;
import com.pja.bloodcount.dto.response.CaseResponse;
import com.pja.bloodcount.model.Case;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CaseMapper {

    public static CaseResponse mapToResponseDTO(Case aCase) {
        return CaseResponse
                .builder()
                .id(aCase.getId())
                .firstMinAge(aCase.getFirstMinAge())
                .firstMaxAge(aCase.getFirstMaxAge())
                .secondMinAge(aCase.getSecondMinAge())
                .secondMaxAge(aCase.getSecondMaxAge())
                .anemiaType(aCase.getAnemiaType())
                .affectedGender(aCase.getAffectedGender())
                .diagnosis(aCase.getDiagnosis())
                .abnormalities(AbnormalityMapper.mapToAbnormalityDTOList(aCase.getAbnormalities()))
                .hr(aCase.getHr())
                .rr(aCase.getRr())
                .description(aCase.getDescription())
                .infoCom(aCase.getInfoCom())
                .language(aCase.getLanguage())
                .caseName(aCase.getCaseName())
                .bmi(aCase.getBmi())
                .height(aCase.getHeight())
                .bodyMass(aCase.getBodyMass())
                .build();
    }

    public static List<CaseResponse> mapToResponseListDTO(List<Case> cases) {
        return cases.stream()
                .map(aCase -> CaseResponse
                        .builder()
                        .id(aCase.getId())
                        .firstMinAge(aCase.getFirstMinAge())
                        .firstMaxAge(aCase.getFirstMaxAge())
                        .secondMinAge(aCase.getSecondMinAge())
                        .secondMaxAge(aCase.getSecondMaxAge())
                        .anemiaType(aCase.getAnemiaType())
                        .affectedGender(aCase.getAffectedGender())
                        .diagnosis(aCase.getDiagnosis())
                        .hr(aCase.getHr())
                        .rr(aCase.getRr())
                        .description(aCase.getDescription())
                        .infoCom(aCase.getInfoCom())
                        .language(aCase.getLanguage())
                        .caseName(aCase.getCaseName())
                        .bmi(aCase.getBmi())
                        .height(aCase.getHeight())
                        .bodyMass(aCase.getBodyMass())
                        .abnormalities(AbnormalityMapper.mapToAbnormalityDTOList(aCase.getAbnormalities()))
                        .build())
                .toList();
    }

    public static CaseOfGameResponse mapToCaseOfGameResponseDTO(Case aCase) {
        return CaseOfGameResponse
                .builder()
                .id(aCase.getId())
                .anemiaType(aCase.getAnemiaType())
                .diagnosis(aCase.getDiagnosis())
                .hr(aCase.getHr())
                .rr(aCase.getRr())
                .description(aCase.getDescription())
                .infoCom(aCase.getInfoCom())
                .language(aCase.getLanguage())
                .caseName(aCase.getCaseName())
                .bmi(aCase.getBmi())
                .height(aCase.getHeight())
                .bodyMass(aCase.getBodyMass())
                .build();
    }

    public static Case mapRequestToEntity(CreateCaseRequest request) {
        return Case
                .builder()
                .firstMinAge(request.getFirstMinAge())
                .firstMaxAge(request.getFirstMaxAge())
                .secondMinAge(request.getSecondMinAge())
                .secondMaxAge(request.getSecondMaxAge())
                .anemiaType(request.getAnemiaType())
                .affectedGender(request.getAffectedGender())
                .diagnosis(request.getDiagnosis())
                .hr(request.getHr())
                .rr(request.getRr())
                .description(request.getDescription())
                .infoCom(request.getInfoCom())
                .language(request.getLanguage())
                .caseName(request.getCaseName())
                .bmi(request.getBmi())
                .height(request.getHeight())
                .bodyMass(request.getBodyMass())
                .build();
    }
}
