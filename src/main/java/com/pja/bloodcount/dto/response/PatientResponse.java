package com.pja.bloodcount.dto.response;

import com.pja.bloodcount.model.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatientResponse {

    private Long id;
    private Gender gender;
    private int age;
    private List<BloodCountResponse> bloodCounts = new ArrayList<>();
}
