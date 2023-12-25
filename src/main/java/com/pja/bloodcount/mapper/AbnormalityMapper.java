package com.pja.bloodcount.mapper;

import com.pja.bloodcount.dto.request.CreateAbnormalityRequest;
import com.pja.bloodcount.dto.response.AbnormalityResponse;
import com.pja.bloodcount.model.BloodCountAbnormality;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AbnormalityMapper {

    public static BloodCountAbnormality mapToBloodCountAbnormality(CreateAbnormalityRequest abnormalityRequest) {
        return BloodCountAbnormality
                .builder()
                .parameter(abnormalityRequest.getParameter())
                .unit(abnormalityRequest.getUnit())
                .minValue(abnormalityRequest.getMinValue())
                .maxValue(abnormalityRequest.getMaxValue())
                .type(abnormalityRequest.getType())
                .build();
    }

    public static List<AbnormalityResponse> mapToAbnormalityDTOList(List<BloodCountAbnormality> abnormalities){
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
