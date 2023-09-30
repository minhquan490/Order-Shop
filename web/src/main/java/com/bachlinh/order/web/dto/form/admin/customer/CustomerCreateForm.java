package com.bachlinh.order.web.dto.form.admin.customer;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.fasterxml.jackson.annotation.JsonAlias;

@ActiveReflection
public class CustomerCreateForm implements ValidatedDto {

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

    @JsonAlias("role")
    private String role;

    @JsonAlias("username")
    private String username;

    @JsonAlias("password")
    private String password;

    @JsonAlias("address")
    private AddressAttribute address;

    @ActiveReflection
    public CustomerCreateForm() {
    }

    @ActiveReflection
    public void setFirstName(String firstName) {
        this.firstName = firstName;
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
    public void setEmail(String email) {
        this.email = email;
    }

    @ActiveReflection
    public void setGender(String gender) {
        this.gender = gender;
    }

    @ActiveReflection
    public void setRole(String role) {
        this.role = role;
    }

    @ActiveReflection
    public void setUsername(String username) {
        this.username = username;
    }

    @ActiveReflection
    public void setPassword(String password) {
        this.password = password;
    }

    @ActiveReflection
    public void setAddress(AddressAttribute address) {
        this.address = address;
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

    public String getRole() {
        return this.role;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public AddressAttribute getAddress() {
        return this.address;
    }

    @ActiveReflection
    public static class AddressAttribute {

        @JsonAlias("province")
        private String province;

        @JsonAlias("district")
        private String district;

        @JsonAlias("ward")
        private String ward;

        @JsonAlias("house_address")
        private String houseAddress;

        @ActiveReflection
        public AddressAttribute() {
        }

        @ActiveReflection
        public void setProvince(String province) {
            this.province = province;
        }

        @ActiveReflection
        public void setDistrict(String district) {
            this.district = district;
        }

        @ActiveReflection
        public void setWard(String ward) {
            this.ward = ward;
        }

        @ActiveReflection
        public void setHouseAddress(String houseAddress) {
            this.houseAddress = houseAddress;
        }

        public String getProvince() {
            return this.province;
        }

        public String getDistrict() {
            return this.district;
        }

        public String getWard() {
            return this.ward;
        }

        public String getHouseAddress() {
            return this.houseAddress;
        }
    }
}
