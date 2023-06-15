package com.pja.bloodcount.dto.response;

import com.pja.bloodcount.model.enums.LevelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BloodCountResponse {

    private Long id;
    private String parameter;
    private String unit;
    private Double value;
    private String referenceValueRange;
    private LevelType levelType;
}
