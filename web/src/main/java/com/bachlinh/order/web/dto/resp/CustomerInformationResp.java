package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import com.bachlinh.order.entity.model.Customer;

import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
public class CustomerInformationResp {

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("phone")
    private String phoneNumber;

    @JsonProperty("email")
    private String email;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("addresses")
    private Collection<String> address;

    public static CustomerInformationResp toDto(Customer customer) {
        CustomerInformationResp dto = new CustomerInformationResp();
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        if (customer.getAddresses() == null || customer.getAddresses().isEmpty()) {
            dto.setAddress(Collections.emptyList());
        } else {
            dto.setAddress(customer.getAddresses().stream().map(a -> String.join(",", a.getValue(), a.getCity(), a.getCountry())).toList());
        }
        dto.setGender(customer.getGender().toLowerCase());
        dto.setEmail(customer.getEmail());
        dto.setPhoneNumber(customer.getPhoneNumber());
        return dto;
    }
}
