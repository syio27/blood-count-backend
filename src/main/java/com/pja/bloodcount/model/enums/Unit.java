package com.pja.bloodcount.model.enums;

public enum Unit {
    GIGALITER("10^9/L"),
    TERALITER("10^12/L"),
    GRAMS_PER_DECILITER("g/dL"),
    FEMTOLITERS("fl"),
    PICOGRAMS("pg"),
    PERCENTAGE("%");

    private final String symbol;

    Unit(String symbol) {
        this.symbol = symbol;
    }

    public String symbol() {
        return symbol;
    }
}
