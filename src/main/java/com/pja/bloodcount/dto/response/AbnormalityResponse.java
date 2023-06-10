package com.pja.bloodcount.dto.response;

import com.pja.bloodcount.model.enums.LevelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class AbnormalityResponse {

    private Long id;
    private String parameter;
    private String unit;
    private Double minValue;
    private Double maxValue;
    private LevelType type;
}
