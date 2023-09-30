package com.bachlinh.order.web.dto.form.customer;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.fasterxml.jackson.annotation.JsonAlias;

@ActiveReflection
public class CustomerUpdateForm implements ValidatedDto {

    @JsonAlias("id")
    private String id;

    @JsonAlias("first_name")
    private String firstName;

    @JsonAlias("last_name")
    private String lastName;

    @JsonAlias("phone")
    private String phone;

    @JsonAlias("email")
    private String email;

    @JsonAlias("gender")
    private String gender;

    @JsonAlias("username")
    private String username;

    @ActiveReflection
    public CustomerUpdateForm() {
    }

    @ActiveReflection
    public void setId(String id) {
        this.id = id;
    }

    @ActiveReflection
    public void setEmail(String email) {
        this.email = email;
    }

    @ActiveReflection
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @ActiveReflection
    public void setGender(String gender) {
        this.gender = gender;
    }

    @ActiveReflection
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @ActiveReflection
    public void setPhone(String phone) {
        this.phone = phone;
    }

    @ActiveReflection
    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return this.id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getPhone() {
        return this.phone;
    }

    public String getEmail() {
        return this.email;
    }

    public String getGender() {
        return this.gender;
    }

    public String getUsername() {
        return this.username;
    }
}
