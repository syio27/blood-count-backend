package com.pja.bloodcount.model.enums;

import lombok.Getter;

@Getter
public enum Pages {
    ONE(1, "One"),
    TWO(2, "Two"),
    THREE(3, "Three"),
    FOUR(4, "Four");

    private final int numericValue;
    private final String stringValue;

    Pages(int numericValue, String stringValue) {
        this.numericValue = numericValue;
        this.stringValue = stringValue;
    }

    public Pages getNextPage() {
        int nextOrdinal = (this.ordinal() + 1) % values().length;
        return values()[nextOrdinal];
    }

    public static Pages next(Pages currentPage) {
        if (currentPage == FOUR) {
            return currentPage;
        }
        return currentPage.getNextPage();
    }
}
