package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import com.bachlinh.order.entity.model.Customer;

@RequiredArgsConstructor
public class TableCustomerInfoResp {
    private final Customer delegate;

    @JsonProperty("id")
    public String id() {
        return this.delegate.getId();
    }

    @JsonProperty("username")
    public String username() {
        return this.delegate.getUsername();
    }

    @JsonProperty("name")
    public String name() {
        return String.join(" ", this.delegate.getFirstName(), this.delegate.getLastName());
    }

    @JsonProperty("phone")
    public String phone() {
        return this.delegate.getPhoneNumber();
    }

    @JsonProperty("email")
    public String email() {
        return this.delegate.getEmail();
    }
}
