package com.pja.bloodcount.utils;

import com.pja.bloodcount.exceptions.RangeArgumentException;

public class RangeValidationUtils {
    
    private RangeValidationUtils() {
        // private constructor
    }

    public static void validateRanges(int firstMinAge, int firstMaxAge,
                                int secondMinAge, int secondMaxAge) throws RangeArgumentException {
        if (firstMinAge < 18 || firstMinAge >= firstMaxAge) {
            throw new RangeArgumentException("First min age should be higher than or equal to 18");
        }
        if (firstMaxAge > 75) {
            throw new RangeArgumentException("First max age should be less than or equal to 75");
        }
        if (secondMinAge < 18 || secondMinAge >= secondMaxAge) {
            throw new RangeArgumentException("Second min age should be higher than or equal to 18");
        }
        if (secondMaxAge > 75) {
            throw new RangeArgumentException("Second max age should be less than or equal to 75");
        }
        if (firstMinAge > secondMinAge) {
            throw new RangeArgumentException("First min age cannot be higher than second min age");
        }
        if (firstMaxAge > secondMaxAge) {
            throw new RangeArgumentException("First max age cannot be higher than second max age");
        }
        if (firstMaxAge > secondMinAge) {
            throw new RangeArgumentException("First max age should be less than Second min age");
        }
    }

    public static void validateRanges(int firstMinAge, int firstMaxAge) throws RangeArgumentException {
        if (firstMinAge < 18 || firstMinAge >= firstMaxAge) {
            throw new RangeArgumentException("First min age should be higher than or equal to 18");
        }
        if (firstMaxAge > 75) {
            throw new RangeArgumentException("First max age should be less than or equal to 75");
        }
    }
}
