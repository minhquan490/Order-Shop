package com.bachlinh.order.web.dto.resp;

import com.bachlinh.order.entity.model.Customer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.Collection;

@JsonRootName("user")
public class CustomerResp {

    @JsonProperty("id")
    private String id;

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

    @JsonProperty("role")
    private String role;

    @JsonProperty("username")
    private String username;

    @JsonProperty("address")
    private Collection<String> address;

    @JsonProperty("is_activated")
    private boolean activated;

    @JsonProperty("is_account_non_expired")
    private boolean accountNonExpired;

    @JsonProperty("is_account_non_locked")
    private boolean accountNonLocked;

    @JsonProperty("is_credentials_non_expired")
    private boolean credentialsNonExpired;

    @JsonProperty("is_enabled")
    private boolean enabled;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Collection<String> getAddress() {
        return address;
    }

    public void setAddress(Collection<String> address) {
        this.address = address;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public static CustomerResp toDto(Customer customer) {
        CustomerResp dto = new CustomerResp();
        dto.setId(customer.getId());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setPhoneNumber(customer.getPhoneNumber());
        dto.setEmail(customer.getEmail());
        dto.setGender(customer.getGender().toLowerCase());
        dto.setUsername(customer.getUsername());
        dto.setAddress(customer.getAddresses().stream().map(a -> String.join(",", a.getValue(), a.getCity(), a.getCountry())).toList());
        dto.setActivated(customer.isActivated());
        dto.setAccountNonExpired(customer.isAccountNonExpired());
        dto.setAccountNonLocked(customer.isAccountNonLocked());
        dto.setCredentialsNonExpired(customer.isCredentialsNonExpired());
        dto.setEnabled(customer.isEnabled());
        return dto;
    }
}
