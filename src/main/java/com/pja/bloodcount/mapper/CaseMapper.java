package com.pja.bloodcount.mapper;

import com.pja.bloodcount.dto.response.AbnormalityResponse;
import com.pja.bloodcount.dto.response.CaseOfGameResponse;
import com.pja.bloodcount.dto.response.CaseResponse;
import com.pja.bloodcount.model.BloodCountAbnormality;
import com.pja.bloodcount.model.Case;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
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
                .abnormalities(mapToAbnormalityDTOList(aCase.getAbnormalities()))
                .hr(aCase.getHr())
                .rr(aCase.getHr())
                .infoCom(aCase.getInfoCom())
                .language(aCase.getLanguage())
                .caseName(aCase.getCaseName())
                .bmi(aCase.getBmi())
                .height(aCase.getHeight())
                .bodyMass(aCase.getBodyMass())
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
                        .hr(aCase.getHr())
                        .rr(aCase.getHr())
                        .infoCom(aCase.getInfoCom())
                        .language(aCase.getLanguage())
                        .caseName(aCase.getCaseName())
                        .bmi(aCase.getBmi())
                        .height(aCase.getHeight())
                        .bodyMass(aCase.getBodyMass())
                        .abnormalities(mapToAbnormalityDTOList(aCase.getAbnormalities()))
                        .build())
                .toList();
    }

    public static CaseOfGameResponse mapToCaseOfGameResponseDTO(Case aCase){
        return CaseOfGameResponse
                .builder()
                .id(aCase.getId())
                .anemiaType(aCase.getAnemiaType())
                .diagnosis(aCase.getDiagnosis())
                .hr(aCase.getHr())
                .rr(aCase.getRr())
                .infoCom(aCase.getInfoCom())
                .language(aCase.getLanguage())
                .caseName(aCase.getCaseName())
                .bmi(aCase.getBmi())
                .height(aCase.getHeight())
                .bodyMass(aCase.getBodyMass())
                .build();
    }

    private static List<AbnormalityResponse> mapToAbnormalityDTOList(List<BloodCountAbnormality> abnormalities){
        if(abnormalities != null){
            return abnormalities.stream()
                    .map(abnormality -> AbnormalityResponse
                            .builder()
                            .id(abnormality.getId())
                            .parameter(abnormality.getParameter())
                            .unit(abnormality.getUnit())
                            .minValue(abnormality.getMinValue())
                            .maxValue(abnormality.getMaxValue())
                            .type(abnormality.getType())
                            .build())
                    .toList();
        }
        return new ArrayList<>();
    }
}
