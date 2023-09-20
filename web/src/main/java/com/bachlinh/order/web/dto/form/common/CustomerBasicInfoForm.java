package com.bachlinh.order.web.dto.form.common;

import com.fasterxml.jackson.annotation.JsonAlias;

public class CustomerBasicInfoForm {

    @JsonAlias("token")
    private String token;

    public CustomerBasicInfoForm() {
    }

    public String getToken() {
        return this.token;
    }

    @JsonAlias("token")
    public void setToken(String token) {
        this.token = token;
    }
}
