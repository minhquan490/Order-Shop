package com.bachlinh.order.web.dto.resp;

import com.bachlinh.order.entity.model.Customer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.Collection;

@JsonRootName("user")
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Collection<String> getAddress() {
        return address;
    }

    public void setAddress(Collection<String> address) {
        this.address = address;
    }

    public static CustomerInformationResp toDto(Customer customer) {
        CustomerInformationResp dto = new CustomerInformationResp();
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setAddress(customer.getAddresses().stream().map(a -> String.join(",", a.getValue(), a.getCity(), a.getCountry())).toList());
        dto.setGender(customer.getGender().toLowerCase());
        dto.setEmail(customer.getEmail());
        dto.setPhoneNumber(customer.getPhoneNumber());
        return dto;
    }
}
