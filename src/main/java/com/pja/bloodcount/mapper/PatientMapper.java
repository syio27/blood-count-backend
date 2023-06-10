package com.pja.bloodcount.mapper;

import com.pja.bloodcount.dto.response.BloodCountResponse;
import com.pja.bloodcount.dto.response.PatientResponse;
import com.pja.bloodcount.model.BloodCount;
import com.pja.bloodcount.model.Patient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PatientMapper {

    public static PatientResponse mapToResponseDTO(Patient patient) {
        return PatientResponse
                .builder()
                .id(patient.getId())
                .gender(patient.getGender())
                .age(patient.getAge())
                .bloodCounts(mapToBloodCountDTOList(patient.getBloodCounts()))
                .build();
    }

    public static List<PatientResponse> mapToResponseListDTO(List<Patient> patients) {
        return patients.stream()
                .map(patient -> PatientResponse
                        .builder()
                        .id(patient.getId())
                        .gender(patient.getGender())
                        .age(patient.getAge())
                        .bloodCounts(mapToBloodCountDTOList(patient.getBloodCounts()))
                        .build())
                .toList();
    }

    private static List<BloodCountResponse> mapToBloodCountDTOList(List<BloodCount> bloodCounts) {
        if (bloodCounts != null) {
            return bloodCounts.stream()
                    .map(bloodCount -> BloodCountResponse
                            .builder()
                            .id(bloodCount.getId())
                            .parameter(bloodCount.getParameter())
                            .unit(bloodCount.getUnit())
                            .value(bloodCount.getValue())
                            .build())
                    .toList();
        }
        return new ArrayList<>();
    }
}