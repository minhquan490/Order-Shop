package com.bachlinh.order.web.dto.strategy;

import org.springframework.security.crypto.password.PasswordEncoder;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.dto.strategy.AbstractDtoStrategy;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.enums.Country;
import com.bachlinh.order.entity.enums.Gender;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.entity.model.Address;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.parser.AddressParser;
import com.bachlinh.order.web.dto.form.admin.customer.CustomerCreateForm;

@ActiveReflection
public class CustomerStrategy extends AbstractDtoStrategy<Customer, CustomerCreateForm> {
    private EntityFactory entityFactory;
    private PasswordEncoder passwordEncoder;

    @ActiveReflection
    public CustomerStrategy(DependenciesResolver dependenciesResolver, Environment environment) {
        super(dependenciesResolver, environment);
    }

    @Override
    protected void beforeConvert(CustomerCreateForm source, Class<Customer> type) {
        // Do nothing
    }

    @Override
    protected Customer doConvert(CustomerCreateForm source, Class<Customer> type) {
        var customer = entityFactory.getEntity(Customer.class);
        customer.setFirstName(source.getFirstName());
        customer.setLastName(source.getLastName());
        customer.setPhoneNumber(source.getPhone());
        customer.setEmail(source.getEmail());
        customer.setGender(Gender.of(source.getGender()).name());
        customer.setRole(Role.of(source.getRole()).name());
        customer.setUsername(source.getUsername());
        customer.setPassword(passwordEncoder.encode(source.getPassword()));
        var addressForm = source.getAddress();
        var address = entityFactory.getEntity(Address.class);
        address.setCustomer(customer);
        address.setCountry(Country.VIET_NAM.getCountry());
        address.setCity(addressForm.getProvince());
        address.setValue(AddressParser.parseVietNamAddress(addressForm.getHouseAddress(), addressForm.getWard(), addressForm.getDistrict(), addressForm.getProvince()));
        customer.getAddresses().add(address);
        return customer;
    }

    @Override
    protected void inject() {
        if (entityFactory == null) {
            entityFactory = getDependenciesResolver().resolveDependencies(EntityFactory.class);
        }
        if (passwordEncoder == null) {
            passwordEncoder = getDependenciesResolver().resolveDependencies(PasswordEncoder.class);
        }
    }

    @Override
    protected void afterConvert(CustomerCreateForm source, Class<Customer> type) {
        // Do nothing
    }

    @Override
    public Class<Customer> getTargetType() {
        return Customer.class;
    }
}
