package com.pja.bloodcount.service;

import com.pja.bloodcount.dto.response.AbnormalityResponse;
import com.pja.bloodcount.dto.response.CaseResponse;
import com.pja.bloodcount.exceptions.GenderGenerationException;
import com.pja.bloodcount.exceptions.PatientBloodCountConflictException;
import com.pja.bloodcount.model.BloodCount;
import com.pja.bloodcount.model.BloodCountReference;
import com.pja.bloodcount.model.Patient;
import com.pja.bloodcount.model.enums.AffectedGender;
import com.pja.bloodcount.model.enums.Gender;
import com.pja.bloodcount.repository.BloodCountRepository;
import com.pja.bloodcount.repository.PatientRepository;
import com.pja.bloodcount.service.contract.BCReferenceService;
import com.pja.bloodcount.service.contract.CaseService;
import com.pja.bloodcount.utils.CalculateBCUtil;
import com.pja.bloodcount.validation.PatientValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
@AllArgsConstructor
public class GenerationService {

    private final PatientRepository patientRepository;
    private final CaseService caseService;
    private final BCReferenceService referenceService;
    private final PatientValidator patientValidator;
    private final BloodCountRepository bloodCountRepository;
    private final CalculateBCUtil calculateBCUtil;

    public Patient generatePatient (Long caseId) {
        CaseResponse aCase = caseService.getCaseWithAbnormalities(caseId);
        int age = aCase.getSecondMinAge() == 0 && aCase.getSecondMaxAge() == 0 ?
                randomizeAge(aCase.getFirstMinAge(), aCase.getFirstMaxAge()) :
                randomizeAge(aCase.getFirstMinAge(), aCase.getFirstMaxAge(),
                             aCase.getSecondMinAge(), aCase.getSecondMaxAge());

        Gender gender = ramdomizeGender(aCase.getAffectedGender());
        return patientRepository.save(
                Patient
                .builder()
                        .age(age)
                        .gender(gender)
                        .bloodCounts(new ArrayList<>())
                .build());
    }

    public void generateBloodCount (Long caseId, Long patientId) {
        CaseResponse aCase = caseService.getCaseWithAbnormalities(caseId);
        Patient patient = patientValidator.validateIfExistsAndGet(patientId);
        List<AbnormalityResponse> caseAbnormalities = aCase.getAbnormalities();
        List<BloodCountReference> referenceTable = referenceService.fullTableOfBCReference();


        // Check if patient already has Blood Count attached to it
        if(bloodCountRepository.findByParameterAndUnitAndPatient("WBC", "10^9/L", patient) != null){
            throw new PatientBloodCountConflictException(patientId);
        }

        // create blood count table with normal values from reference table
        for(BloodCountReference reference : referenceTable){
            double value = 0d;
            // if blood count's value doest need to be calculated, then it will be randomly generated based on the range
            if(!needsToBeCalculated(reference.getParameter(), reference.getUnit())){
                // check the patient gender, if male generate value from male value range or otherwise
                if(patient.getGender().equals(Gender.FEMALE)){
                    value = randomizeValue(reference.getParameter(), reference.getUnit(), reference.getMinFemale(), reference.getMaxFemale());
                }
                if(patient.getGender().equals(Gender.MALE)){
                    value = randomizeValue(reference.getParameter(), reference.getUnit(), reference.getMinMale(), reference.getMaxMale());
                }

                // create blood count instance, add to patient and save it to DataBase
                BloodCount bloodCount = BloodCount
                        .builder()
                        .parameter(reference.getParameter())
                        .unit(reference.getUnit())
                        .value(Math.round(value * 10.0) / 10.0)
                        .build();
                patient.addBloodCount(bloodCount);
                patientRepository.save(patient);

                // if value of BC has to be calculated, then use calculat### methods to calculate the value of BC
            } else {
                value = switch (reference.getParameter()) {
                    case "MCV" -> calculateBCUtil.calculateMCV("HCT", "%", "RBC", "10^12/L", patient);
                    case "MCH" -> calculateBCUtil.calculateMCH("HGB", "g/dl", "RBC", "10^12/L", patient);
                    case "MCHC" -> calculateBCUtil.calculateMCHC("HGB", "g/dl", "HCT", "%", patient);
                    case "NEU" -> calculateBCUtil.calculateCommon("NEU", "10^9/L", "WBC", "10^9/L", patient);
                    case "LYM" -> calculateBCUtil.calculateCommon("LYM", "10^9/L", "WBC", "10^9/L", patient);
                    case "MONO" -> calculateBCUtil.calculateCommon("MONO", "10^9/L", "WBC", "10^9/L", patient);
                    case "EOS" -> calculateBCUtil.calculateCommon("EOS", "10^9/L", "WBC", "10^9/L", patient);
                    case "BASO" -> calculateBCUtil.calculateCommon("BASO", "10^9/L", "WBC", "10^9/L", patient);
                    default -> 0d;
                };
                // create blood count instance, add to patient and save it to DataBase
                BloodCount bloodCount = BloodCount
                        .builder()
                        .parameter(reference.getParameter())
                        .unit(reference.getUnit())
                        .value(Math.round(value * 10.0) / 10.0)
                        .build();
                patient.addBloodCount(bloodCount);
                patientRepository.save(patient);
            }

            log.info("Randomized value of blood-count - {} is: {}", reference.getParameter(), value);
        }
        log.info("Patients blood count normal value generation end");

        // Adjust the value of blood count if bloodCount in generatedNormalValueBloodCount is present in caseAbnormalities
        List<BloodCount> generatedNormalValueBloodCount = patient.getBloodCounts();
        for(BloodCount bloodCount : generatedNormalValueBloodCount){

            Optional<AbnormalityResponse> matchingAbnormality = caseAbnormalities.stream()
                    .filter(abnormalityResponse -> abnormalityResponse.getParameter().equals(bloodCount.getParameter()) && abnormalityResponse.getUnit().equals(bloodCount.getUnit()))
                    .findFirst();
            if (matchingAbnormality.isPresent()) {
                log.info("Blood Count - {} with unit - {} needs to be adjusted by abnormality", bloodCount.getParameter(), bloodCount.getUnit());
                double value = randomizeValue(bloodCount.getParameter(), bloodCount.getUnit(), matchingAbnormality.get().getMinValue(), matchingAbnormality.get().getMaxValue());
                log.info("Adjusted value us -> {}", value);
                bloodCount.setValue(Math.round(value * 10.0) / 10.0);
                bloodCountRepository.save(bloodCount);
            }
        }
    }

    private boolean needsToBeCalculated(String parameter, String unit) {
        HashMap<String, String> bloodCountMap = new HashMap<>();
        bloodCountMap.put("MCV", "fl");
        bloodCountMap.put("MCH", "pg");
        bloodCountMap.put("MCHC", "g/dl");
        bloodCountMap.put("NEU", "%");
        bloodCountMap.put("LYM", "%");
        bloodCountMap.put("MONO", "%");
        bloodCountMap.put("EOS", "%");
        bloodCountMap.put("BASO", "%");

        return bloodCountMap.containsKey(parameter) && bloodCountMap.containsValue(unit);
    }

    private Double randomizeValue(String parameter, String unit, Double min, Double max) {
        if("RDW_CV".equals(parameter)){
            log.info("Min value: {}, Max value: {}", min, max);
            return ThreadLocalRandom.current().nextDouble(min, max);
        }
        if("%".equals(unit)){
            int intMin = min.intValue();
            int intMax = max.intValue();
            log.info("Min value: {}, Max value: {}", intMin, intMax);
            return (double) ThreadLocalRandom.current().nextInt(intMin, intMax + 1);
        }
        log.info("Min value: {}, Max value: {}", min, max);
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    private int randomizeAge(int minAge, int maxAge){
        return ThreadLocalRandom.current().nextInt(minAge, maxAge + 1);
    }

    private int randomizeAge(int firstMinAge, int firstMaxAge,
                             int secondMinAge, int secondMaxAge){
        int ageFromFirstRange = ThreadLocalRandom.current().nextInt(firstMinAge, firstMaxAge + 1);
        int ageFromSecondRange = ThreadLocalRandom.current().nextInt(secondMinAge, secondMaxAge + 1);
        return ThreadLocalRandom.current().nextBoolean() ? ageFromFirstRange : ageFromSecondRange;
    }

    private Gender ramdomizeGender(AffectedGender affectedGender){
        if(affectedGender == null){
            throw new GenderGenerationException("ERROR: affected gender is passed as null");
        }
        if (affectedGender.equals(AffectedGender.FEMALE)){
            return Gender.FEMALE;
        }
        if(affectedGender.equals(AffectedGender.MALE)){
            return Gender.MALE;
        }
        if(affectedGender.equals(AffectedGender.BOTH)){
            return ThreadLocalRandom.current().nextBoolean() ? Gender.MALE : Gender.FEMALE;
        }
        return null;
    }
}
