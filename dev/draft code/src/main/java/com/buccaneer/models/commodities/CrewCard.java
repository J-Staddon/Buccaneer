package com.buccaneer.models.commodities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CrewCard implements ICommodity {
    private boolean isRed;
    private int value;

    public CrewCard() {}

    /**
     * Holds value of a crew card
     * @param value
     * @param isRed
     */
    public CrewCard(int value, boolean isRed) {
        this.value = value;
        this.isRed = isRed;
    }

    /**
     * @return
     */
    public int getValue() {
        return value;
    }

    /**
     * @return
     */
    public boolean isRed() {
        return isRed;
    }
}
