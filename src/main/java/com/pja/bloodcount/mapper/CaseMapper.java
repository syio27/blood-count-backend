package com.pja.bloodcount.mapper;

import com.pja.bloodcount.dto.response.AbnormalityResponse;
import com.pja.bloodcount.dto.response.CaseResponse;
import com.pja.bloodcount.model.BloodCountAbnormality;
import com.pja.bloodcount.model.Case;

import java.util.ArrayList;
import java.util.List;

public class CaseMapper {

    public static CaseResponse mapToResponseDTO(Case aCase){
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
                .abnormalities(mapToAbnormalityDTO(aCase.getAbnormalities()))
                .build();
    }

    public static List<CaseResponse> mapToResponseListDTO(List<Case> cases){
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
                        .abnormalities(mapToAbnormalityDTO(aCase.getAbnormalities()))
                        .build())
                .toList();
    }

    private static List<AbnormalityResponse> mapToAbnormalityDTO(List<BloodCountAbnormality> abnormalities){
        if(abnormalities != null){
            return abnormalities.stream()
                    .map(abnormality -> AbnormalityResponse
                            .builder()
                            .id(abnormality.getId())
                            .parameter(abnormality.getParameter())
                            .minValue(abnormality.getMinValue())
                            .maxValue(abnormality.getMaxValue())
                            .type(abnormality.getType())
                            .build())
                    .toList();
        }
        return new ArrayList<>();
    }
}
