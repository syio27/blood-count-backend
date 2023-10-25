package com.pja.bloodcount.utils;

import com.pja.bloodcount.exceptions.GenderGenerationException;
import com.pja.bloodcount.model.BloodCountReference;
import com.pja.bloodcount.model.Patient;
import com.pja.bloodcount.model.enums.AffectedGender;
import com.pja.bloodcount.model.enums.Gender;

import java.util.concurrent.ThreadLocalRandom;

/**
 * util class for generating fields values of BloodCount entity
 */
public class RandomizeUtil {

    private static final String BC_PARAMETER = "RDW_CV";
    private static final String BC_UNIT = "%";

    private RandomizeUtil() {
        // private constructor
    }

    public static Double randomizeValue(String parameter, String unit, Double min, Double max) {
        if (BC_PARAMETER.equals(parameter)) {
            return randomizeDouble(min, max);
        }
        if (BC_UNIT.equals(unit)) {
            int intMin = min.intValue();
            int intMax = max.intValue();
            return (double) randomizeInt(intMin, intMax + 1);
        }
        return randomizeDouble(min, max);
    }

    public static int randomizeAge(int minAge, int maxAge) {
        return randomizeInt(minAge, maxAge + 1);
    }

    public static int randomizeAge(int firstMinAge, int firstMaxAge,
                             int secondMinAge, int secondMaxAge) {
        int ageFromFirstRange = randomizeInt(firstMinAge, firstMaxAge + 1);
        int ageFromSecondRange = randomizeInt(secondMinAge, secondMaxAge + 1);
        return randomizeBoolean() ? ageFromFirstRange : ageFromSecondRange;
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
            return randomizeBoolean() ? Gender.MALE : Gender.FEMALE;
        }
        return null;
    }

    public static double randomizeValueBasedOnGender(BloodCountReference reference, Patient patient) {
        if (patient.getGender().equals(Gender.FEMALE)) {
            return randomizeValue(reference.getParameter(), reference.getUnit(), reference.getMinFemale(), reference.getMaxFemale());
        }
        return randomizeValue(reference.getParameter(), reference.getUnit(), reference.getMinMale(), reference.getMaxMale());
    }
    
    private static int randomizeInt(int origin, int bound) {
        return  ThreadLocalRandom.current().nextInt(origin, bound + 1);
    }

    private static double randomizeDouble(Double origin, Double bound) {
        return  ThreadLocalRandom.current().nextDouble(origin, bound + 1);
    }

    private static boolean randomizeBoolean() {
        return  ThreadLocalRandom.current().nextBoolean();
    }
}
