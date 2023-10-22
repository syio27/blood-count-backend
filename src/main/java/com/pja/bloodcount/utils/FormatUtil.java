package com.pja.bloodcount.utils;

import java.text.DecimalFormat;

public class FormatUtil {

    private FormatUtil() {
        // private constructor
    }

    public static Double roundFormat(double value) {
        DecimalFormat df = new DecimalFormat("#.##");
        String formattedNumber = df.format(value).replace(",", ".");
        return Double.parseDouble(formattedNumber);
    }
}
