package com.buccaneer.models.commodities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Treasure implements ICommodity {
    private String name;
    private int value;

    public Treasure() {}

    /**
     * Holds name and value of treasure
     * @param name
     * @param value
     */
    public Treasure(String name, int value) {
        this.name = name;
        this.value = value;
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @return
     */
    public int getValue() {
        return value;
    }
}
