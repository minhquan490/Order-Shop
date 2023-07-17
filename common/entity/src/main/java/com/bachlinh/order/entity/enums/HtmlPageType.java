package com.bachlinh.order.entity.enums;

public enum HtmlPageType {
    DESKTOP("desktop"),
    MOBILE("mobile"),
    TABLET("tablet");

    private final String value;

    HtmlPageType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
