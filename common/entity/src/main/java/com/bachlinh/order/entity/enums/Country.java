package com.bachlinh.order.entity.enums;

public enum Country {
    VIET_NAM("Viet Nam");

    private final String country;

    Country(String country) {
        this.country = country;
    }

    public String getCountry() {
        return country;
    }
}
