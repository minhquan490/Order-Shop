package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.Address;
import com.bachlinh.order.entity.model.Address_;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.AddressRepository;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.repository.query.Operator;
import com.bachlinh.order.repository.query.Select;
import com.bachlinh.order.repository.query.SqlBuilder;
import com.bachlinh.order.repository.query.SqlSelect;
import com.bachlinh.order.repository.query.SqlWhere;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.repository.utils.QueryUtils;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@RepositoryComponent
@ActiveReflection
public class AddressRepositoryImpl extends AbstractRepository<String, Address> implements AddressRepository {

    @DependenciesInitialize
    @ActiveReflection
    public AddressRepositoryImpl(@NonNull DependenciesContainerResolver containerResolver) {
        super(Address.class, containerResolver.getDependenciesResolver());
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public Address composeSave(@NonNull Address address, @NonNull CustomerRepository customerRepository) {
        String customerId = address.getCustomer().getId();
        if (customerRepository.isCustomerIdExisted(customerId)) {
            customerRepository.saveCustomer(address.getCustomer());
        }
        return Optional.of(this.save(address)).orElse(null);
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public Address updateAddress(Address address) {
        return this.save(address);
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public boolean deleteAddress(Address address) {
        delete(address);
        return true;
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public void bulkSave(Collection<Address> addresses) {
        saveAll(addresses);
    }

    @Override
    public Address getAddressForUpdate(String id) {
        Select idSelect = Select.builder().column(Address_.ID).build();
        Select valueSelect = Select.builder().column(Address_.VALUE).build();
        Select citySelect = Select.builder().column(Address_.CITY).build();
        Select countrySelect = Select.builder().column(Address_.COUNTRY).build();
        Where idWhere = Where.builder().attribute(Address_.ID).value(id).operator(Operator.EQ).build();
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
    @PersistenceContext
    @ActiveReflection
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
