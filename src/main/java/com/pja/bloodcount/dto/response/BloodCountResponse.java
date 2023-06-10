package com.pja.bloodcount.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BloodCountResponse {

    private Long id;
    private String parameter;
    private String unit;
    private Double value;
}
