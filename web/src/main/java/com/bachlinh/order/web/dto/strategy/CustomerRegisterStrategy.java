package com.bachlinh.order.web.dto.strategy;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.dto.strategy.AbstractDtoStrategy;
import com.bachlinh.order.dto.strategy.DtoStrategy;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.core.enums.Role;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.web.dto.form.customer.RegisterForm;
import org.springframework.security.crypto.password.PasswordEncoder;

@ActiveReflection
public class CustomerRegisterStrategy extends AbstractDtoStrategy<Customer, RegisterForm> {

    private EntityFactory entityFactory;
    private PasswordEncoder passwordEncoder;

    @ActiveReflection
    private CustomerRegisterStrategy(DependenciesResolver dependenciesResolver, Environment environment) {
        super(dependenciesResolver, environment);
    }

    @Override
    protected void beforeConvert(RegisterForm source, Class<Customer> type) {
        if (entityFactory == null) {
            entityFactory = getDependenciesResolver().resolveDependencies(EntityFactory.class);
        }
        if (passwordEncoder == null) {
            passwordEncoder = getDependenciesResolver().resolveDependencies(PasswordEncoder.class);
        }
    }

    @Override
    protected Customer doConvert(RegisterForm source, Class<Customer> type) {
        Customer customer = entityFactory.getEntity(type);
        customer.setUsername(source.email());
        customer.setEmail(source.email());
        customer.setPassword(passwordEncoder.encode(source.password()));
        customer.setOrderPoint(0);
        customer.setRole(Role.CUSTOMER.name());
        return customer;
    }

    @Override
    protected void afterConvert(RegisterForm source, Class<Customer> type) {
        // Do nothing
    }

    @Override
    protected DtoStrategy<Customer, RegisterForm> createNew(DependenciesResolver dependenciesResolver, Environment environment) {
        return new CustomerRegisterStrategy(dependenciesResolver, environment);
    }

    @Override
    public Class<Customer> getTargetType() {
        return Customer.class;
    }
}
