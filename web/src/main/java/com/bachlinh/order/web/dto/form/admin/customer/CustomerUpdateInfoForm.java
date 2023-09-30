package com.bachlinh.order.web.dto.form.admin.customer;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.fasterxml.jackson.annotation.JsonAlias;

@ActiveReflection
public class CustomerUpdateInfoForm implements ValidatedDto {

    @JsonAlias("id")
    private String id;

    @JsonAlias("first_name")
    private String firstName;

    @JsonAlias("last_name")
    private String lastName;

    @JsonAlias("phone")
    private String phoneNumber;

    @JsonAlias("email")
    private String email;

    @JsonAlias("gender")
    private String gender;

    @JsonAlias("role")
    private String role;

    @JsonAlias("order_point")
    private Integer orderPoint;

    @JsonAlias("activated")
    private Boolean activated;

    @JsonAlias("account_non_expired")
    private Boolean accountNonExpired;

    @JsonAlias("account_non_locked")
    private Boolean accountNonLocked;

    @JsonAlias("credentials_non_expired")
    private Boolean credentialsNonExpired;

    @JsonAlias("enabled")
    private Boolean enabled;

    @JsonAlias("addresses")
    private Address[] addresses;

    @ActiveReflection
    public CustomerUpdateInfoForm() {
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

    public String getPhoneNumber() {
        return this.phoneNumber;
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

    public Integer getOrderPoint() {
        return this.orderPoint;
    }

    public Boolean getActivated() {
        return this.activated;
    }

    public Boolean getAccountNonExpired() {
        return this.accountNonExpired;
    }

    public Boolean getAccountNonLocked() {
        return this.accountNonLocked;
    }

    public Boolean getCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    public Boolean getEnabled() {
        return this.enabled;
    }

    public Address[] getAddresses() {
        return this.addresses;
    }

    @JsonAlias("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonAlias("first_name")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @JsonAlias("last_name")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @JsonAlias("phone")
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @JsonAlias("email")
    public void setEmail(String email) {
        this.email = email;
    }

    @JsonAlias("gender")
    public void setGender(String gender) {
        this.gender = gender;
    }

    @JsonAlias("role")
    public void setRole(String role) {
        this.role = role;
    }

    @JsonAlias("order_point")
    public void setOrderPoint(Integer orderPoint) {
        this.orderPoint = orderPoint;
    }

    @JsonAlias("activated")
    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    @JsonAlias("account_non_expired")
    public void setAccountNonExpired(Boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    @JsonAlias("account_non_locked")
    public void setAccountNonLocked(Boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    @JsonAlias("credentials_non_expired")
    public void setCredentialsNonExpired(Boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    @JsonAlias("enabled")
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @JsonAlias("addresses")
    public void setAddresses(Address[] addresses) {
        this.addresses = addresses;
    }

    public static class Address {

        @JsonAlias("house_number")
        private String houseNumber;

        @JsonAlias("ward")
        private String wardId;

        @JsonAlias("district")
        private String districtId;

        @JsonAlias("province")
        private String provinceId;

        public String getHouseNumber() {
            return this.houseNumber;
        }

        public String getWardId() {
            return this.wardId;
        }

        public String getDistrictId() {
            return this.districtId;
        }

        public String getProvinceId() {
            return this.provinceId;
        }

        @JsonAlias("house_number")
        public void setHouseNumber(String houseNumber) {
            this.houseNumber = houseNumber;
        }

        @JsonAlias("ward")
        public void setWardId(String wardId) {
            this.wardId = wardId;
        }

        @JsonAlias("district")
        public void setDistrictId(String districtId) {
            this.districtId = districtId;
        }

        @JsonAlias("province")
        public void setProvinceId(String provinceId) {
            this.provinceId = provinceId;
        }
    }
}
