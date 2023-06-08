package com.pja.bloodcount.dto.response;

import com.pja.bloodcount.model.BloodCountAbnormality;
import com.pja.bloodcount.model.enums.AffectedGender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CaseResponse {

    private Long id;
    private int firstMinAge;
    private int firstMaxAge;
    private int secondMinAge;
    private int secondMaxAge;
    private AffectedGender affectedGender;
    private String anemiaType;
    private String diagnosis;
    private List<AbnormalityResponse> abnormalities = new ArrayList<>();
}
