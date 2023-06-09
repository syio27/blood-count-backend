package com.pja.bloodcount.service;

import com.pja.bloodcount.dto.response.CaseResponse;
import com.pja.bloodcount.exceptions.GenderGenerationException;
import com.pja.bloodcount.model.Patient;
import com.pja.bloodcount.model.enums.AffectedGender;
import com.pja.bloodcount.model.enums.Gender;
import com.pja.bloodcount.repository.PatientRepository;
import com.pja.bloodcount.service.contract.CaseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
@AllArgsConstructor
public class GenerationService {

    private final PatientRepository patientRepository;
    private final CaseService caseService;

    public Patient generatePatient(Long caseId){
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
                .build());
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
