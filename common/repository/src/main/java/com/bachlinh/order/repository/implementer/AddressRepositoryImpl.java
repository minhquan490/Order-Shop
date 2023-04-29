package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.entity.model.Address;
import com.bachlinh.order.entity.model.Address_;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.AddressRepository;
import com.bachlinh.order.repository.CustomerRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;

@Repository
@Primary
class AddressRepositoryImpl extends AbstractRepository<Address, String> implements AddressRepository {

    @Autowired
    AddressRepositoryImpl(@NonNull ApplicationContext applicationContext) {
        super(Address.class, applicationContext);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Address composeSave(@NonNull Address address, @NonNull CustomerRepository customerRepository) {
        String customerId = address.getCustomer().getId();
        if (customerRepository.existById(customerId)) {
            customerRepository.saveCustomer(address.getCustomer());
        }
        return Optional.of(this.save(address)).orElse(null);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY, isolation = READ_COMMITTED)
    public Address updateAddress(Address address) {
        return this.save(address);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean deleteAddress(Address address) {
        long numRowDeleted = this.delete(Specification.where(((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Address_.ID), address.getId()))));
        return numRowDeleted == 0;
    }

    @Override
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
