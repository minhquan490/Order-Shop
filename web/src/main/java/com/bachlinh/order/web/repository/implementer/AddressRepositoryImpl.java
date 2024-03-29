package com.bachlinh.order.web.repository.implementer;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DependenciesInitialize;
import com.bachlinh.order.core.annotation.RepositoryComponent;
import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.entity.model.Address;
import com.bachlinh.order.entity.model.Address_;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.web.repository.spi.AbstractRepository;
import com.bachlinh.order.repository.RepositoryBase;
import com.bachlinh.order.repository.query.Operation;
import com.bachlinh.order.repository.query.Select;
import com.bachlinh.order.repository.query.SqlBuilder;
import com.bachlinh.order.repository.query.SqlSelect;
import com.bachlinh.order.repository.query.SqlWhere;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.repository.utils.QueryUtils;
import com.bachlinh.order.web.repository.spi.AddressRepository;
import com.bachlinh.order.web.repository.spi.CustomerRepository;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@RepositoryComponent
@ActiveReflection
public class AddressRepositoryImpl extends AbstractRepository<String, Address> implements AddressRepository, RepositoryBase {

    @DependenciesInitialize
    @ActiveReflection
    public AddressRepositoryImpl(@NonNull DependenciesContainerResolver containerResolver) {
        super(Address.class, containerResolver.getDependenciesResolver());
    }

    @Override
    public Address composeSave(@NonNull Address address, @NonNull CustomerRepository customerRepository) {
        String customerId = address.getCustomer().getId();
        if (customerRepository.isCustomerIdExisted(customerId)) {
            customerRepository.saveCustomer(address.getCustomer());
        }
        return Optional.of(this.save(address)).orElse(null);
    }

    @Override
    public Address updateAddress(Address address) {
        return this.save(address);
    }

    @Override
    public boolean deleteAddress(Address address) {
        delete(address);
        return true;
    }

    @Override
    public void bulkSave(Collection<Address> addresses) {
        saveAll(addresses);
    }

    @Override
    public Address getAddressForUpdate(String id) {
        Select idSelect = Select.builder().column(Address_.ID).build();
        Select valueSelect = Select.builder().column(Address_.VALUE).build();
        Select citySelect = Select.builder().column(Address_.CITY).build();
        Select countrySelect = Select.builder().column(Address_.COUNTRY).build();
        Where idWhere = Where.builder().attribute(Address_.ID).value(id).operation(Operation.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Address.class);
        sqlSelect.select(idSelect)
                .select(valueSelect)
                .select(citySelect)
                .select(countrySelect);
        SqlWhere sqlWhere = sqlSelect.where(idWhere);
        String query = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return getSingleResult(query, attributes, getDomainClass());
    }

    @Override
    public Collection<Address> getAddressOfCustomer(Customer owner) {
        Where ownerWhere = Where.builder().attribute(Address_.CUSTOMER).value(owner).operation(Operation.EQ).build();

        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(getDomainClass());
        SqlWhere sqlWhere = sqlSelect.where(ownerWhere);

        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());

        return getResultList(sql, attributes, getDomainClass());
    }

    @Override
    public void deleteAddresses(Collection<Address> addresses) {
        deleteAll(addresses);
    }

    @Override
    public RepositoryBase getInstance(DependenciesContainerResolver containerResolver) {
        return new AddressRepositoryImpl(containerResolver);
    }

    @Override
    public Class<?>[] getRepositoryTypes() {
        return new Class[]{AddressRepository.class};
    }
}
