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
import com.pja.bloodcount.model.enums.LevelType;
import com.pja.bloodcount.repository.BloodCountRepository;
import com.pja.bloodcount.repository.PatientRepository;
import com.pja.bloodcount.service.contract.BCReferenceService;
import com.pja.bloodcount.service.contract.CaseService;
import com.pja.bloodcount.utils.CalculateBCUtil;
import com.pja.bloodcount.validation.PatientValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;
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

    public Patient generatePatient(Long caseId) {
        CaseResponse aCase = caseService.getCaseWithAbnormalities(caseId);
        int age = aCase.getSecondMinAge() == 0 && aCase.getSecondMaxAge() == 0 ?
                randomizeAge(aCase.getFirstMinAge(), aCase.getFirstMaxAge()) :
                randomizeAge(aCase.getFirstMinAge(), aCase.getFirstMaxAge(),
                        aCase.getSecondMinAge(), aCase.getSecondMaxAge());

        Gender gender = randomizeGender(aCase.getAffectedGender());
        return patientRepository.save(
                Patient
                        .builder()
                        .age(age)
                        .gender(gender)
                        .bloodCounts(new ArrayList<>())
                        .build());
    }

    public void generateBloodCount(Long caseId, Long patientId) {
        CaseResponse aCase = caseService.getCaseWithAbnormalities(caseId);
        Patient patient = patientValidator.validateIfExistsAndGet(patientId);
        List<AbnormalityResponse> caseAbnormalities = aCase.getAbnormalities();
        List<BloodCountReference> referenceTable = referenceService.fullTableOfBCReference();


        // Check if patient already has Blood Count attached to it
        if (bloodCountRepository.findByParameterAndUnitAndPatient("WBC", "10^9/L", patient) != null) {
            throw new PatientBloodCountConflictException(patientId);
        }

        List<BloodCount> bloodCounts = new ArrayList<>();

        // First loop: Create and randomize blood counts
        for (BloodCountReference reference : referenceTable) {
            double value = randomizeValueBasedOnGender(reference, patient);
            double roundedValue = roundFormat(value);
            String referenceValueRange = getReferenceValueRange(reference, patient.getGender());
            LevelType levelType = determineLevelType(roundedValue, reference, patient.getGender());

            BloodCount bloodCount = BloodCount.builder()
                    .parameter(reference.getParameter())
                    .unit(reference.getUnit())
                    .value(roundedValue)
                    .referenceValueRange(referenceValueRange)
                    .levelType(levelType)
                    .build();

            bloodCounts.add(bloodCount);
            patient.addBloodCount(bloodCount); // This should update both the patient and the bloodCount thanks to the @OneToMany relationship
            log.info("Randomized value of blood-count - {} is: {}", reference.getParameter(), value);
        }

        // Save all blood counts to database here
        bloodCountRepository.saveAll(bloodCounts);

        // Second loop: Recalculate the necessary parameters
        for (BloodCount bloodCount : bloodCounts) {
            if (needsToBeCalculated(bloodCount.getParameter(), bloodCount.getUnit())) {
                double recalculatedValue = callCalculationUtil(bloodCount.getParameter(), patient);
                bloodCount.setValue(recalculatedValue);
                BloodCountReference reference = findReferenceByParameterAndUnit(bloodCount.getParameter(), bloodCount.getUnit(), referenceTable);
                bloodCount.setLevelType(determineLevelType(recalculatedValue, reference, patient.getGender()));
            }
        }

        // Save updated blood counts and patient to database
        bloodCountRepository.saveAll(bloodCounts);
        patientRepository.save(patient);

        log.info("Patients blood count normal value generation end");

        // Adjust the value of blood count if bloodCount in generatedNormalValueBloodCount is present in caseAbnormalities
        List<BloodCount> generatedNormalValueBloodCount = patient.getBloodCounts();
        log.info("abnormality size: {}", caseAbnormalities.size());
        for (BloodCount bloodCount : generatedNormalValueBloodCount) {

            Optional<AbnormalityResponse> matchingAbnormality = caseAbnormalities.stream()
                    .filter(abnormalityResponse -> abnormalityResponse.getParameter().equals(bloodCount.getParameter()) && abnormalityResponse.getUnit().equals(bloodCount.getUnit()))
                    .findFirst();
            if (matchingAbnormality.isPresent()) {
                log.info("Blood Count - {} with unit - {} needs to be adjusted by abnormality", bloodCount.getParameter(), bloodCount.getUnit());
                double value = randomizeValue(bloodCount.getParameter(), bloodCount.getUnit(), matchingAbnormality.get().getMinValue(), matchingAbnormality.get().getMaxValue());
                double roundedValue = roundFormat(value);
                log.info("Adjusted value is -> {}", roundedValue);
                bloodCount.setValue(roundedValue);
                String[] referenceRange = bloodCount.getReferenceValueRange().split(" - ");
                double minValue = Double.parseDouble(referenceRange[0]);
                double maxValue = Double.parseDouble(referenceRange[1]);
                LevelType levelType = LevelType.NORMAL;
                if (roundedValue < minValue) {
                    levelType = LevelType.DECREASED;
                } else if (roundedValue > maxValue) {
                    levelType = LevelType.INCREASED;
                }
                bloodCount.setLevelType(levelType);
                bloodCountRepository.save(bloodCount);
            }
        }
        log.info("Recalculating BC");
        // Recalculate by abnormalities change
        List<BloodCount> adjustedByAbnoBCList = patient.getBloodCounts();
        for (BloodCount bloodCount : adjustedByAbnoBCList) {
            double roundedValue;

            if (needsToBeCalculated(bloodCount.getParameter(), bloodCount.getUnit())) {
                roundedValue = callCalculationUtil(bloodCount.getParameter(), patient);
                bloodCount.setValue(roundedValue);
                String[] referenceRange = bloodCount.getReferenceValueRange().split(" - ");
                double minValue = Double.parseDouble(referenceRange[0]);
                double maxValue = Double.parseDouble(referenceRange[1]);
                LevelType levelType = LevelType.NORMAL;
                if (roundedValue < minValue) {
                    levelType = LevelType.DECREASED;
                } else if (roundedValue > maxValue) {
                    levelType = LevelType.INCREASED;
                }
                bloodCount.setLevelType(levelType);
                bloodCountRepository.save(bloodCount);
            }
        }
    }

    private boolean needsToBeCalculated(String parameter, String unit) {
        HashSet<String> bloodCountSet = new HashSet<>();
        bloodCountSet.add("RBC-10^12/L");
        bloodCountSet.add("HCT-%");
        bloodCountSet.add("MCHC-g/dl");

        bloodCountSet.add("NEU-10^9/L");
        bloodCountSet.add("LYM-10^9/L");
        bloodCountSet.add("MONO-10^9/L");
        bloodCountSet.add("EOS-10^9/L");
        bloodCountSet.add("BASO-10^9/L");

        return bloodCountSet.contains(parameter + "-" + unit);
    }

    private Double randomizeValue(String parameter, String unit, Double min, Double max) {
        if ("RDW_CV".equals(parameter)) {
            log.info("Min value: {}, Max value: {}", min, max);
            return ThreadLocalRandom.current().nextDouble(min, max);
        }
        if ("%".equals(unit)) {
            int intMin = min.intValue();
            int intMax = max.intValue();
            log.info("Min value: {}, Max value: {}", intMin, intMax);
            return (double) ThreadLocalRandom.current().nextInt(intMin, intMax + 1);
        }
        log.info("Min value: {}, Max value: {}", min, max);
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    private int randomizeAge(int minAge, int maxAge) {
        return ThreadLocalRandom.current().nextInt(minAge, maxAge + 1);
    }

    private int randomizeAge(int firstMinAge, int firstMaxAge,
                             int secondMinAge, int secondMaxAge) {
        int ageFromFirstRange = ThreadLocalRandom.current().nextInt(firstMinAge, firstMaxAge + 1);
        int ageFromSecondRange = ThreadLocalRandom.current().nextInt(secondMinAge, secondMaxAge + 1);
        return ThreadLocalRandom.current().nextBoolean() ? ageFromFirstRange : ageFromSecondRange;
    }

    private Gender randomizeGender(AffectedGender affectedGender) {
        if (affectedGender == null) {
            throw new GenderGenerationException("ERROR: affected gender is passed as null");
        }
        if (affectedGender.equals(AffectedGender.FEMALE)) {
            return Gender.FEMALE;
        }
        if (affectedGender.equals(AffectedGender.MALE)) {
            return Gender.MALE;
        }
        if (affectedGender.equals(AffectedGender.BOTH)) {
            return ThreadLocalRandom.current().nextBoolean() ? Gender.MALE : Gender.FEMALE;
        }
        return null;
    }

    private Double roundFormat(double value) {
        System.out.println("Formatting value: " + value);
        DecimalFormat df = new DecimalFormat("#.##");
        String formattedNumber = df.format(value).replace(",", ".");
        return Double.parseDouble(formattedNumber);
    }

    private double callCalculationUtil(String parameter, Patient patient) {
        double value = switch (parameter) {
            case "RBC" -> calculateBCUtil.calculateRBC("HGB", "g/dl", "MCH", "pg", patient);
            case "HCT" -> calculateBCUtil.calculateHCT("MCV", "fl", "RBC", "10^12/L", patient);
            case "MCHC" -> calculateBCUtil.calculateMCHC("HGB", "g/dl", "HCT", "%", patient);

            case "NEU" -> calculateBCUtil.calculateCommon("NEU", "%", "WBC", "10^9/L", patient);
            case "LYM" -> calculateBCUtil.calculateCommon("LYM", "%", "WBC", "10^9/L", patient);
            case "MONO" -> calculateBCUtil.calculateCommon("MONO", "%", "WBC", "10^9/L", patient);
            case "EOS" -> calculateBCUtil.calculateCommon("EOS", "%", "WBC", "10^9/L", patient);
            case "BASO" -> calculateBCUtil.calculateCommon("BASO", "%", "WBC", "10^9/L", patient);
            default -> 0d;
        };
        return roundFormat(value);
    }

    private double randomizeValueBasedOnGender(BloodCountReference reference, Patient patient) {
        if (patient.getGender().equals(Gender.FEMALE)) {
            return randomizeValue(reference.getParameter(), reference.getUnit(), reference.getMinFemale(), reference.getMaxFemale());
        }
        return randomizeValue(reference.getParameter(), reference.getUnit(), reference.getMinMale(), reference.getMaxMale());
    }

    private String getReferenceValueRange(BloodCountReference reference, Gender gender) {
        return gender.equals(Gender.FEMALE) ?
                reference.getMinFemale() + " - " + reference.getMaxFemale() :
                reference.getMinMale() + " - " + reference.getMaxMale();
    }

    private LevelType determineLevelType(double value, BloodCountReference reference, Gender gender) {
        double min = gender.equals(Gender.FEMALE) ? reference.getMinFemale() : reference.getMinMale();
        double max = gender.equals(Gender.FEMALE) ? reference.getMaxFemale() : reference.getMaxMale();

        if (value < min) return LevelType.DECREASED;
        if (value > max) return LevelType.INCREASED;
        return LevelType.NORMAL;
    }

    private BloodCountReference findReferenceByParameterAndUnit(String parameter, String unit, List<BloodCountReference> referenceTable) {
        for (BloodCountReference reference : referenceTable) {
            if (reference.getParameter().equals(parameter) && reference.getUnit().equals(unit)) {
                return reference;
            }
        }
        return null;
    }
}
