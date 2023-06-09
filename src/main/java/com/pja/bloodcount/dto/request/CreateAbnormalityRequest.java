package com.pja.bloodcount.dto.request;

import com.pja.bloodcount.model.enums.LevelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class CreateAbnormalityRequest {

    private String parameter;
    private Double minValue;
    private Double maxValue;
    private LevelType type;
}
