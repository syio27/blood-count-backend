package com.pja.bloodcount.utils;

import com.pja.bloodcount.exceptions.GenderGenerationException;
import com.pja.bloodcount.model.BloodCountReference;
import com.pja.bloodcount.model.Patient;
import com.pja.bloodcount.model.enums.AffectedGender;
import com.pja.bloodcount.model.enums.Gender;

import java.util.concurrent.ThreadLocalRandom;

public class RandomizeUtil {

    private RandomizeUtil() {
        // private constructor
    }

    public static Double randomizeValue(String parameter, String unit, Double min, Double max) {
        if ("RDW_CV".equals(parameter)) {
            return ThreadLocalRandom.current().nextDouble(min, max);
        }
        if ("%".equals(unit)) {
            int intMin = min.intValue();
            int intMax = max.intValue();
            return (double) ThreadLocalRandom.current().nextInt(intMin, intMax + 1);
        }
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    public static int randomizeAge(int minAge, int maxAge) {
        return ThreadLocalRandom.current().nextInt(minAge, maxAge + 1);
    }

    public static int randomizeAge(int firstMinAge, int firstMaxAge,
                             int secondMinAge, int secondMaxAge) {
        int ageFromFirstRange = ThreadLocalRandom.current().nextInt(firstMinAge, firstMaxAge + 1);
        int ageFromSecondRange = ThreadLocalRandom.current().nextInt(secondMinAge, secondMaxAge + 1);
        return ThreadLocalRandom.current().nextBoolean() ? ageFromFirstRange : ageFromSecondRange;
    }

    public static Gender randomizeGender(AffectedGender affectedGender) {
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

    public static double randomizeValueBasedOnGender(BloodCountReference reference, Patient patient) {
        if (patient.getGender().equals(Gender.FEMALE)) {
            return RandomizeUtil.randomizeValue(reference.getParameter(), reference.getUnit(), reference.getMinFemale(), reference.getMaxFemale());
        }
        return RandomizeUtil.randomizeValue(reference.getParameter(), reference.getUnit(), reference.getMinMale(), reference.getMaxMale());
    }
}
