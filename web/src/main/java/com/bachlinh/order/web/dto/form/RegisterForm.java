package com.bachlinh.order.web.dto.form;

import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.enums.Gender;
import com.bachlinh.order.entity.model.Customer;
import com.fasterxml.jackson.annotation.JsonAlias;
import org.springframework.security.crypto.password.PasswordEncoder;

public record RegisterForm(@JsonAlias("username") String username,
                           @JsonAlias("first_name") String firstName,
                           @JsonAlias("last_name") String lastName,
                           @JsonAlias("phone") String phoneNumber,
                           @JsonAlias("email") String email,
                           @JsonAlias("gender") String gender,
                           @JsonAlias("password") String password) {
    public Customer toCustomer(EntityFactory entityFactory, PasswordEncoder passwordEncoder) {
        Customer customer = entityFactory.getEntity(Customer.class);
        customer.setUsername(this.username());
        customer.setFirstName(this.firstName());
        customer.setLastName(this.lastName());
        customer.setEmail(this.email());
        customer.setPhoneNumber(this.phoneNumber());
        customer.setPassword(passwordEncoder.encode(this.password()));
        customer.setGender(Gender.of(this.gender()).name());
        return customer;
    }
}
