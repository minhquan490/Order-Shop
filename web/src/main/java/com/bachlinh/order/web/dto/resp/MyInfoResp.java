package com.bachlinh.order.web.dto.resp;

import com.bachlinh.order.annotation.Dto;
import com.bachlinh.order.annotation.MappedDtoField;

import java.util.Collection;

@Dto(forType = "com.bachlinh.order.entity.model.Customer")
public class MyInfoResp {

    @MappedDtoField(targetField = "firstName", outputJsonField = "first_name")
    private String firstName;

    @MappedDtoField(targetField = "lastName", outputJsonField = "last_name")
    private String lastName;

    @MappedDtoField(targetField = "phoneNumber", outputJsonField = "phone")
    private String phoneNumber;

    @MappedDtoField(targetField = "email", outputJsonField = "email")
    private String email;

    @MappedDtoField(targetField = "gender", outputJsonField = "gender")
    private String gender;

    @MappedDtoField(targetField = "addressString", outputJsonField = "addresses")
    private Collection<String> address;

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public String getEmail() {
        return this.email;
    }

    public String getGender() {
        return this.gender;
    }

    public Collection<String> getAddress() {
        return this.address;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAddress(Collection<String> address) {
        this.address = address;
    }
}
