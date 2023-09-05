package com.pja.bloodcount.utils;

import com.pja.bloodcount.model.Patient;
import com.pja.bloodcount.repository.BloodCountRepository;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;

/**
 * Calculator util to calculate the BloodCount value
 * @author baglan
 * @version 1.0
 * */
@Service
public class CalculateBCUtil {


    private final BloodCountRepository bloodCountRepository;

    public CalculateBCUtil(BloodCountRepository bloodCountRepository) {
        this.bloodCountRepository = bloodCountRepository;
    }

    public Double calculateRBC(String parameter1, String unit1,
                                String parameter2, String unit2,
                                Patient patient) {
        return bloodCountRepository.findByParameterAndUnitAndPatient(parameter1, unit1, patient).getValue()
                * 10
                / bloodCountRepository.findByParameterAndUnitAndPatient(parameter2, unit2, patient).getValue();
    }

    public Double calculateHCT(String parameter1, String unit1,
                                String parameter2, String unit2,
                                Patient patient) {
        return bloodCountRepository.findByParameterAndUnitAndPatient(parameter1, unit1, patient).getValue()
                * bloodCountRepository.findByParameterAndUnitAndPatient(parameter2, unit2, patient).getValue()
                / 10;
    }

    public Double calculateMCHC(String parameter1, String unit1,
                                 String parameter2, String unit2,
                                 Patient patient) {
        Double valueParam1 = bloodCountRepository.findByParameterAndUnitAndPatient(parameter1, unit1, patient).getValue();
        Double valueParam2 = bloodCountRepository.findByParameterAndUnitAndPatient(parameter2, unit2, patient).getValue();
        return valueParam1
                * 100
                / valueParam2;
    }

    public Double calculateCommon(String parameter1, String unit1,
                                   String parameter2, String unit2,
                                   Patient patient) {
        return bloodCountRepository.findByParameterAndUnitAndPatient(parameter1, unit1, patient).getValue()
                / 100
                * bloodCountRepository.findByParameterAndUnitAndPatient(parameter2, unit2, patient).getValue();
    }
}
