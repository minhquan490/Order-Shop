package com.bachlinh.order.core.entity.validator.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.core.entity.model.Customer;
import com.bachlinh.order.core.entity.validator.spi.AbstractValidator;
import com.bachlinh.order.core.entity.validator.spi.ValidateResult;
import com.bachlinh.order.repository.CustomerRepository;
import org.hibernate.validator.internal.util.DomainNameUtil;
import org.springframework.context.ApplicationContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

@ActiveReflection
public class CustomerValidator extends AbstractValidator<Customer> {
    private CustomerRepository customerRepository;

    @ActiveReflection
    public CustomerValidator(ApplicationContext context) {
        super(context);
    }

    @Override
    public ValidateResult validate(Customer entity) {
        if (customerRepository == null) {
            customerRepository = getApplicationContext().getBean(CustomerRepository.class);
        }
        ValidateResult result = ValidateResult.getInstance();
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
        if (new InternalEmailValidator().isValid(entity.getEmail())) {
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

    private static class InternalEmailValidator {
        private static final String LOCAL_PART_ATOM = "[a-z0-9!#$%&'*+/=?^_`{|}~\u0080-\uFFFF-]";
        private static final String LOCAL_PART_INSIDE_QUOTES_ATOM = "(?:[a-z0-9!#$%&'*.(),<>\\[\\]:;  @+/=?^_`{|}~\u0080-\uFFFF-]|\\\\\\\\|\\\\\\\")";
        private static final Pattern LOCAL_PART_PATTERN = Pattern.compile("(?:" + LOCAL_PART_ATOM + "+|\"" + LOCAL_PART_INSIDE_QUOTES_ATOM + "+\")" + "(?:\\." + "(?:" + LOCAL_PART_ATOM + "+|\"" + LOCAL_PART_INSIDE_QUOTES_ATOM + "+\")" + ")*", CASE_INSENSITIVE);

        public boolean isValid(String value) {
            int splitPosition = value.lastIndexOf('@');
            if (splitPosition < 0) {
                return false;
            }
            String localPart = value.substring(0, splitPosition);
            String domainPart = value.substring(splitPosition + 1);
            if (!isValidEmailLocalPart(localPart)) {
                return false;
            }
            return DomainNameUtil.isValidEmailDomainAddress(domainPart);
        }

        private boolean isValidEmailLocalPart(String localPart) {
            Matcher matcher = LOCAL_PART_PATTERN.matcher(localPart);
            return matcher.matches();
        }
    }
}
