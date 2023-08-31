package com.pja.bloodcount.model.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Affected gender is the gender of future patient who is affected by particular case
 */
public enum AffectedGender {

    FEMALE,
    MALE,
    BOTH;

    private static final Map<String, AffectedGender> polishToEnglishMap = new HashMap<>();

    static {
        polishToEnglishMap.put("KOBIETA", AffectedGender.FEMALE);
        polishToEnglishMap.put("MĘŻCZYZNA", AffectedGender.MALE);
        polishToEnglishMap.put("OBIE", AffectedGender.BOTH);
    }

    public static AffectedGender fromPolishString(String polishString) {
        return polishToEnglishMap.getOrDefault(polishString, null);
    }
}
