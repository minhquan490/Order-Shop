package com.bachlinh.order.web.dto.form;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRootName;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.enums.Gender;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.entity.model.Cart;
import com.bachlinh.order.entity.model.Customer;

/**
 * @deprecated will be remove in next version
 */
@JsonRootName("customer")
@Deprecated(forRemoval = true, since = "1.0.0")
public class CrudCustomerForm {

    @JsonIgnore
    private String id;

    @JsonAlias("username")
    private String username;

    @JsonAlias("password")
    private String password;

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

    @JsonIgnore
    private String page;

    @JsonIgnore
    private String pageSize;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPage() {
        return page;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public static Customer toCustomer(CrudCustomerForm form, EntityFactory factory, PasswordEncoder encoder) {
        Customer customer = factory.getEntity(Customer.class);
        customer.setUsername(form.getUsername());
        customer.setPassword(encoder.encode(form.getPassword()));
        customer.setFirstName(form.getFirstName());
        customer.setLastName(form.getLastName());
        customer.setPhoneNumber(form.phoneNumber);
        customer.setEmail(form.getEmail());
        customer.setGender(Gender.of(form.getGender()).name());
        customer.setRole(Role.of("admin").name());
        Cart cart = factory.getEntity(Cart.class);
        cart.setCustomer(customer);
        customer.setCart(cart);
        return customer;
    }
}
