package com.pja.bloodcount.service;

import com.pja.bloodcount.exceptions.ReferenceTableException;
import com.pja.bloodcount.model.BloodCountReference;
import com.pja.bloodcount.model.enums.Parameter;
import com.pja.bloodcount.model.enums.Unit;
import com.pja.bloodcount.repository.BCReferenceRepository;
import com.pja.bloodcount.service.contract.BCReferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BcReferenceServiceImpl implements BCReferenceService {

    private final BCReferenceRepository repository;
    private static final int TABLE_SIZE = 20;

    @Override
    public List<BloodCountReference> populateTable() {
        List<BloodCountReference> allReferenceElements = getAllReferenceElements();
        repository.saveAll(allReferenceElements);
        return allReferenceElements;
    }

    @Override
    public List<BloodCountReference> fullTableOfBCReference() {
        List<BloodCountReference> referenceTable = repository.findAll();
        if (referenceTable.isEmpty() || referenceTable.size() != TABLE_SIZE) {
            throw new ReferenceTableException("Reference Table size is not enough");
        }
        return referenceTable;
    }

    private static List<BloodCountReference> getAllReferenceElements() {
        final BloodCountReference WBC = BloodCountReference
                .builder()
                .parameter(Parameter.WBC.name())
                .unit(Unit.GIGALITER.symbol())
                .minFemale(4.0)
                .maxFemale(11.0)
                .minMale(4.0)
                .maxMale(11.0)
                .build();

        final BloodCountReference RBC = BloodCountReference
                .builder()
                .parameter(Parameter.RBC.name())
                .unit(Unit.TERALITER.symbol())
                .minFemale(4.0)
                .maxFemale(5.2)
                .minMale(4.6)
                .maxMale(5.7)
                .build();

        final BloodCountReference HGB = BloodCountReference
                .builder()
                .parameter(Parameter.HGB.name())
                .unit(Unit.GRAMS_PER_DECILITER.symbol())
                .minFemale(11.5)
                .maxFemale(16.4)
                .minMale(13.5)
                .maxMale(18.0)
                .build();

        final BloodCountReference HCT = BloodCountReference
                .builder()
                .parameter(Parameter.HCT.name())
                .unit(Unit.PERCENTAGE.symbol())
                .minFemale(37.0)
                .maxFemale(47.0)
                .minMale(42.0)
                .maxMale(52.0)
                .build();

        final BloodCountReference MCV = BloodCountReference
                .builder()
                .parameter(Parameter.MCV.name())
                .unit(Unit.FEMTOLITERS.symbol())
                .minFemale(81d)
                .maxFemale(99d)
                .minMale(80d)
                .maxMale(94d)
                .build();

        final BloodCountReference MCH = BloodCountReference
                .builder()
                .parameter(Parameter.MCH.name())
                .unit(Unit.PICOGRAMS.symbol())
                .minFemale(27d)
                .maxFemale(31d)
                .minMale(27d)
                .maxMale(31d)
                .build();

        final BloodCountReference MCHC = BloodCountReference
                .builder()
                .parameter(Parameter.MCHC.name())
                .unit(Unit.GRAMS_PER_DECILITER.symbol())
                .minFemale(33d)
                .maxFemale(47d)
                .minMale(33d)
                .maxMale(37d)
                .build();

        final BloodCountReference PLT = BloodCountReference
                .builder()
                .parameter(Parameter.PLT.name())
                .unit(Unit.GIGALITER.symbol())
                .minFemale(130.0)
                .maxFemale(450.0)
                .minMale(130.0)
                .maxMale(450.0)
                .build();

        final BloodCountReference RDW_CV = BloodCountReference
                .builder()
                .parameter(Parameter.RDW_CV.name())
                .unit(Unit.PERCENTAGE.symbol())
                .minFemale(11.6)
                .maxFemale(14.4)
                .minMale(11.6)
                .maxMale(14.4)
                .build();

        final BloodCountReference RDW_SD = BloodCountReference
                .builder()
                .parameter(Parameter.RDW_SD.name())
                .unit(Unit.FEMTOLITERS.symbol())
                .minFemale(35.1)
                .maxFemale(43.9)
                .minMale(35.1)
                .maxMale(43.9)
                .build();

        final BloodCountReference NEU = BloodCountReference
                .builder()
                .parameter(Parameter.NEU.name())
                .unit(Unit.GIGALITER.symbol())
                .minFemale(2d)
                .maxFemale(7d)
                .minMale(2d)
                .maxMale(7d)
                .build();

        final BloodCountReference LYM = BloodCountReference
                .builder()
                .parameter(Parameter.LYM.name())
                .unit(Unit.GIGALITER.symbol())
                .minFemale(1d)
                .maxFemale(4.5)
                .minMale(1d)
                .maxMale(4.5)
                .build();

        final BloodCountReference MONO = BloodCountReference
                .builder()
                .parameter(Parameter.MONO.name())
                .unit(Unit.GIGALITER.symbol())
                .minFemale(0.19)
                .maxFemale(0.77)
                .minMale(0.19)
                .maxMale(0.77)
                .build();

        final BloodCountReference EOS = BloodCountReference
                .builder()
                .parameter(Parameter.EOS.name())
                .unit(Unit.GIGALITER.symbol())
                .minFemale(0.02)
                .maxFemale(0.5)
                .minMale(0.02)
                .maxMale(0.5)
                .build();

        final BloodCountReference BASO = BloodCountReference
                .builder()
                .parameter(Parameter.BASO.name())
                .unit(Unit.GIGALITER.symbol())
                .minFemale(0.02)
                .maxFemale(0.1)
                .minMale(0.02)
                .maxMale(0.1)
                .build();

        final BloodCountReference NEU_p = BloodCountReference
                .builder()
                .parameter(Parameter.NEU.name())
                .unit(Unit.PERCENTAGE.symbol())
                .minFemale(40d)
                .maxFemale(80d)
                .minMale(40d)
                .maxMale(80d)
                .build();

        final BloodCountReference LYM_p = BloodCountReference
                .builder()
                .parameter(Parameter.LYM.name())
                .unit(Unit.PERCENTAGE.symbol())
                .minFemale(20d)
                .maxFemale(40d)
                .minMale(20d)
                .maxMale(40d)
                .build();

        final BloodCountReference MONO_p = BloodCountReference
                .builder()
                .parameter(Parameter.MONO.name())
                .unit(Unit.PERCENTAGE.symbol())
                .minFemale(2d)
                .maxFemale(10d)
                .minMale(2d)
                .maxMale(10d)
                .build();

        final BloodCountReference EOS_p = BloodCountReference
                .builder()
                .parameter(Parameter.EOS.name())
                .unit(Unit.PERCENTAGE.symbol())
                .minFemale(1d)
                .maxFemale(6d)
                .minMale(1d)
                .maxMale(6d)
                .build();

        final BloodCountReference BASO_p = BloodCountReference
                .builder()
                .parameter(Parameter.BASO.name())
                .unit(Unit.PERCENTAGE.symbol())
                .minFemale(0d)
                .maxFemale(2d)
                .minMale(0d)
                .maxMale(2d)
                .build();

        return List.of(WBC, RBC, HGB, HCT, MCV, MCH, MCHC, PLT,
                RDW_CV, RDW_SD,
                NEU, LYM, MONO, EOS, BASO,
                NEU_p, LYM_p, MONO_p, EOS_p, BASO_p);
    }
}
