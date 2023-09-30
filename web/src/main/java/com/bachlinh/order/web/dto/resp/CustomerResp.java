package com.bachlinh.order.web.dto.resp;

import com.bachlinh.order.core.annotation.Dto;
import com.bachlinh.order.core.annotation.MappedDtoField;

@Dto(forType = "com.bachlinh.order.entity.model.Customer")
public class CustomerResp {

    @MappedDtoField(targetField = "id", outputJsonField = "id")
    private String id;

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

    @MappedDtoField(targetField = "role", outputJsonField = "role")
    private String role;

    @MappedDtoField(targetField = "username", outputJsonField = "username")
    private String username;

    @MappedDtoField(targetField = "activated", outputJsonField = "is_activated")
    private boolean activated;

    @MappedDtoField(targetField = "accountNonExpired", outputJsonField = "is_account_non_expired")
    private boolean accountNonExpired;

    @MappedDtoField(targetField = "accountNonLocked", outputJsonField = "is_account_non_locked")
    private boolean accountNonLocked;

    @MappedDtoField(targetField = "credentialsNonExpired", outputJsonField = "is_credentials_non_expired")
    private boolean credentialsNonExpired;

    @MappedDtoField(targetField = "enabled", outputJsonField = "is_enabled")
    private boolean enabled;

    public CustomerResp() {
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

    public String getUsername() {
        return this.username;
    }

    public boolean isActivated() {
        return this.activated;
    }

    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setRole(String role) {
        this.role = role;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
