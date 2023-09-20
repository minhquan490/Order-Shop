package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomerBasicInformationResp {

    @JsonProperty("id")
    private String customerId;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("role")
    private String role;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    public CustomerBasicInformationResp() {
    }

    public String getCustomerId() {
        return this.customerId;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getRole() {
        return this.role;
    }

    public String getAvatarUrl() {
        return this.avatarUrl;
    }

    @JsonProperty("id")
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    @JsonProperty("first_name")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @JsonProperty("last_name")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @JsonProperty("role")
    public void setRole(String role) {
        this.role = role;
    }

    @JsonProperty("avatar_url")
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
