package com.bachlinh.order.core.enums;

public enum Country {
    VIET_NAM("Viet Nam");

    private final String countryField;

    Country(String country) {
        this.countryField = country;
    }

    public String getCountry() {
        return countryField;
    }
}
