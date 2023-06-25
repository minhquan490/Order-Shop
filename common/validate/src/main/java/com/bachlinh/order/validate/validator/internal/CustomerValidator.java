package com.bachlinh.order.validate.validator.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.ValidateUtils;
import com.bachlinh.order.validate.validator.spi.AbstractValidator;
import com.bachlinh.order.validate.validator.spi.Result;

@ActiveReflection
public class CustomerValidator extends AbstractValidator<Customer> {
    private CustomerRepository customerRepository;

    @ActiveReflection
    public CustomerValidator(DependenciesResolver resolver) {
        super(resolver);
    }

    @Override
    protected void inject() {
        if (customerRepository == null) {
            customerRepository = getResolver().resolveDependencies(CustomerRepository.class);
        }
    }

    @Override
    protected ValidateResult doValidate(Customer entity) {
        ValidateResult result = new Result();
        if (entity.getUsername().length() > 24) {
            result.addMessageError("Username: is greater than 24 character");
        }
        if (entity.getUsername().isBlank()) {
            result.addMessageError("Username: is blank");
        }
        if (customerRepository.usernameExist(entity.getUsername())) {
            result.addMessageError("Username: is existed");
        }
        if (entity.getFirstName().length() > 36) {
            result.addMessageError("First name: is greater than 36 character");
        }
        if (entity.getFirstName().isBlank()) {
            result.addMessageError("First name: is blank");
        }
        if (entity.getLastName().length() > 36) {
            result.addMessageError("Last name: is greater than 36 character");
        }
        if (entity.getLastName().isBlank()) {
            result.addMessageError("Last name: is blank");
        }
        if (entity.getPhoneNumber().length() != 10) {
            result.addMessageError("Phone number: must have 10 number");
        }
        if (customerRepository.phoneNumberExist(entity.getPhoneNumber())) {
            result.addMessageError("Phone number: is existed");
        }
        if (entity.getEmail().length() > 32) {
            result.addMessageError("Email: is greater than 32 character");
        }
        if (entity.getEmail().isBlank()) {
            result.addMessageError("Email: is blank");
        }
        if (ValidateUtils.isEmailValidUsingRfc2822(entity.getEmail())) {
            result.addMessageError("Email: is not valid");
        }
        if (customerRepository.emailExist(entity.getEmail())) {
            result.addMessageError("Email: is exist");
        }
        if (entity.getGender().isEmpty()) {
            result.addMessageError("Gender: must be a male or female");
        }
        return result;
    }
}
