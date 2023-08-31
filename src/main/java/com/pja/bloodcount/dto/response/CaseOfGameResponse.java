package com.pja.bloodcount.dto.response;

import com.pja.bloodcount.model.enums.AffectedGender;
import com.pja.bloodcount.model.enums.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CaseOfGameResponse {

    private Long id;
    private String anemiaType;
    private String diagnosis;
    private String hr;
    private String rr;
    private String infoCom;
    private String caseName;
    private Language language;
    private String bodyMass;
    private String height;
    private String bmi;
}
