package com.pja.bloodcount.service;

import com.pja.bloodcount.dto.response.AbnormalityResponse;
import com.pja.bloodcount.dto.response.CaseResponse;
import com.pja.bloodcount.exceptions.PatientBloodCountConflictException;
import com.pja.bloodcount.model.BloodCount;
import com.pja.bloodcount.model.BloodCountReference;
import com.pja.bloodcount.model.Patient;
import com.pja.bloodcount.model.enums.Gender;
import com.pja.bloodcount.model.enums.LevelType;
import com.pja.bloodcount.model.enums.Parameter;
import com.pja.bloodcount.model.enums.Unit;
import com.pja.bloodcount.repository.BloodCountRepository;
import com.pja.bloodcount.repository.PatientRepository;
import com.pja.bloodcount.service.contract.BCReferenceService;
import com.pja.bloodcount.service.contract.CaseService;
import com.pja.bloodcount.utils.CalculateBCUtil;
import com.pja.bloodcount.utils.FormatUtil;
import com.pja.bloodcount.utils.RandomizeUtil;
import com.pja.bloodcount.validation.PatientValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.*;

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
                RandomizeUtil.randomizeAge(aCase.getFirstMinAge(), aCase.getFirstMaxAge()) :
                RandomizeUtil.randomizeAge(aCase.getFirstMinAge(), aCase.getFirstMaxAge(),
                        aCase.getSecondMinAge(), aCase.getSecondMaxAge());

        Gender gender = RandomizeUtil.randomizeGender(aCase.getAffectedGender());
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


        if (bloodCountRepository.findByParameterAndUnitAndPatient(Parameter.WBC.name(), Unit.GIGALITER.symbol(), patient) != null) {
            throw new PatientBloodCountConflictException(patientId);
        }

        List<BloodCount> bloodCounts = new ArrayList<>();

        for (BloodCountReference reference : referenceTable) {
            double value = RandomizeUtil.randomizeValueBasedOnGender(reference, patient);
            double roundedValue = FormatUtil.roundFormat(value);
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
            patient.addBloodCount(bloodCount);
            log.info("Randomized value of blood-count - {} is: {}", reference.getParameter(), value);
        }

        bloodCountRepository.saveAll(bloodCounts);

        for (BloodCount bloodCount : bloodCounts) {
            if (needsToBeCalculated(bloodCount.getParameter(), bloodCount.getUnit())) {
                double recalculatedValue = doCalculation(bloodCount.getParameter(), patient);
                bloodCount.setValue(recalculatedValue);
                BloodCountReference reference = findReferenceByParameterAndUnit(bloodCount.getParameter(), bloodCount.getUnit(), referenceTable).orElseThrow(EntityNotFoundException::new);
                bloodCount.setLevelType(determineLevelType(recalculatedValue, reference, patient.getGender()));
            }
        }

        bloodCountRepository.saveAll(bloodCounts);
        patientRepository.save(patient);

        log.info("Patients blood count normal value generation end");

        List<BloodCount> generatedNormalValueBloodCount = patient.getBloodCounts();
        log.info("abnormality size: {}", caseAbnormalities.size());
        for (BloodCount bloodCount : generatedNormalValueBloodCount) {

            Optional<AbnormalityResponse> matchingAbnormality = caseAbnormalities.stream()
                    .filter(abnormalityResponse -> abnormalityResponse.getParameter().equals(bloodCount.getParameter()) && abnormalityResponse.getUnit().equals(bloodCount.getUnit()))
                    .findFirst();
            if (matchingAbnormality.isPresent()) {
                log.info("Blood Count - {} with unit - {} needs to be adjusted by abnormality", bloodCount.getParameter(), bloodCount.getUnit());
                double value = RandomizeUtil.randomizeValue(bloodCount.getParameter(), bloodCount.getUnit(), matchingAbnormality.get().getMinValue(), matchingAbnormality.get().getMaxValue());
                double roundedValue = FormatUtil.roundFormat(value);
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
        List<BloodCount> adjustedByAbnoBCList = patient.getBloodCounts();
        for (BloodCount bloodCount : adjustedByAbnoBCList) {
            double roundedValue;

            if (needsToBeCalculated(bloodCount.getParameter(), bloodCount.getUnit())) {
                roundedValue = doCalculation(bloodCount.getParameter(), patient);
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

    private double doCalculation(String parameter, Patient patient) {
        double value = switch (parameter) {
            case "RBC" -> calculateBCUtil.calculateRBC("HGB", Unit.GRAMS_PER_DECILITER.symbol(), "MCH", Unit.PICOGRAMS.symbol(), patient);
            case "HCT" -> calculateBCUtil.calculateHCT("MCV", Unit.FEMTOLITERS.symbol(), "RBC", Unit.TERALITER.symbol(), patient);
            case "MCHC" -> calculateBCUtil.calculateMCHC("HGB", Unit.GRAMS_PER_DECILITER.symbol(), "HCT", Unit.PERCENTAGE.symbol(), patient);
            case "NEU" -> calculateBCUtil.calculateCommon("NEU", Unit.PERCENTAGE.symbol(), "WBC", Unit.GIGALITER.symbol(), patient);
            case "LYM" -> calculateBCUtil.calculateCommon("LYM", Unit.PERCENTAGE.symbol(), "WBC", Unit.GIGALITER.symbol(), patient);
            case "MONO" -> calculateBCUtil.calculateCommon("MONO", Unit.PERCENTAGE.symbol(), "WBC", Unit.GIGALITER.symbol(), patient);
            case "EOS" -> calculateBCUtil.calculateCommon("EOS", Unit.PERCENTAGE.symbol(), "WBC", Unit.GIGALITER.symbol(), patient);
            case "BASO" -> calculateBCUtil.calculateCommon("BASO", Unit.PERCENTAGE.symbol(), "WBC", Unit.GIGALITER.symbol(), patient);
            default -> 0d;
        };
        return FormatUtil.roundFormat(value);
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

    private Optional<BloodCountReference> findReferenceByParameterAndUnit(String parameter, String unit, List<BloodCountReference> referenceTable) {
        for (BloodCountReference reference : referenceTable) {
            if (reference.getParameter().equals(parameter) && reference.getUnit().equals(unit)) {
                return Optional.of(reference);
            }
        }
        return Optional.empty();
    }
}
