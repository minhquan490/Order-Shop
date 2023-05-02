package com.bachlinh.order.repository.implementer;


import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Customer_;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.repository.query.Condition;
import com.bachlinh.order.repository.query.ConditionExecutor;
import com.bachlinh.order.repository.query.Join;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;

@RepositoryComponent
@ActiveReflection
public class CustomerRepositoryImpl extends AbstractRepository<Customer, String> implements CustomerRepository {
    private final AtomicLong customerCount = new AtomicLong(0);

    @DependenciesInitialize
    @ActiveReflection
    public CustomerRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(Customer.class, containerResolver.getDependenciesResolver());
    }

    @Override
    public Customer getCustomerById(String id, boolean useJoin) {
        Specification<Customer> spec = Specification.where(((root, query, criteriaBuilder) -> {
            if (!useJoin) {
                root.join(Customer_.refreshToken, JoinType.INNER);
                root.join(Customer_.addresses, JoinType.LEFT);
            }
            return criteriaBuilder.equal(root.get(Customer_.ID), id);
        }));
        return get(spec);
    }

    @Override
    public Customer getCustomerByUsername(String username) {
        Specification<Customer> spec = Specification.where(((root, query, criteriaBuilder) -> {
            Predicate usernameEqual = criteriaBuilder.equal(root.get(Customer_.USERNAME), username);
            Predicate enableEqual = criteriaBuilder.equal(root.get(Customer_.enabled), true);
            return criteriaBuilder.and(usernameEqual, enableEqual);
        }));
        return get(spec);
    }

    @Override
    public Customer getCustomerByEmail(String email) {
        Specification<Customer> spec = Specification.where(((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Customer_.EMAIL), email)));
        return get(spec);
    }

    @Override
    public Customer getCustomerByPhone(String phone) {
        Specification<Customer> spec = Specification.where(((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Customer_.PHONE_NUMBER), phone)));
        return get(spec);
    }

    @Override
    public Customer getCustomerUseJoin(Object customerId, Collection<Join> joins) {
        Specification<Customer> spec = Specification.where(((root, query, criteriaBuilder) -> {
            joins.forEach(join -> root.join(join.getAttribute(), join.getType()));
            return criteriaBuilder.equal(root.get("id"), customerId);
        }));
        return get(spec);
    }

    @Override
    public long countCustomer() {
        return customerCount.get();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY, isolation = READ_COMMITTED)
    @ActiveReflection
    public boolean deleteCustomer(@NonNull Customer customer) {
        if (StringUtils.hasText((CharSequence) customer.getId())) {
            long numRowDeleted = this.delete(Specification.where((root, query, builder) -> builder.equal(root.get(Customer_.ID), customer.getId())));
            customerCount.set((int) (customerCount.get() - numRowDeleted));
            return numRowDeleted == 1;
        } else {
            return false;
        }
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    @ActiveReflection
    public Customer saveCustomer(@NonNull Customer customer) {
        return this.save(customer);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY, isolation = READ_COMMITTED)
    public Customer updateCustomer(@NonNull Customer customer) {
        return this.saveCustomer(customer);
    }

    @Override
    public boolean usernameExist(String username) {
        return this.getCustomerByUsername(username) != null;
    }

    @Override
    public boolean phoneNumberExist(String phone) {
        return this.getCustomerByPhone(phone) != null;
    }

    @Override
    public boolean emailExist(String email) {
        return this.getCustomerByEmail(email) != null;
    }

    @Override
    public boolean existById(Object customerId) {
        return super.existsById((String) customerId);
    }

    @Override
    public Page<Customer> getAll(Pageable pageable, Sort sort) {
        if (pageable == null && sort == null) {
            List<Customer> results = this.findAll();
            return new PageImpl<>(results, Pageable.unpaged(), results.size());
        }
        if (pageable != null && sort == null) {
            return this.findAll(pageable);
        }
        if (pageable == null) {
            List<Customer> results = this.findAll(sort);
            return new PageImpl<>(results, Pageable.unpaged(), results.size());
        }
        Pageable newPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return this.findAll(newPageable);
    }

    @Override
    public Page<Customer> getCustomersUsingJoin(Collection<Join> joins, Collection<Condition> conditions, Pageable pageable, Sort sort) {
        Specification<Customer> spec = Specification.where(((root, query, criteriaBuilder) -> {
            joins.forEach(join -> root.join(join.getAttribute(), join.getType()));
            return new ConditionExecutor(criteriaBuilder, conditions).execute(root);
        }));
        if (pageable == null && sort == null) {
            List<Customer> results = this.findAll(spec);
            return new PageImpl<>(results, Pageable.unpaged(), results.size());
        }
        if (pageable != null && sort == null) {
            return this.findAll(spec, pageable);
        }
        if (pageable == null) {
            List<Customer> results = this.findAll(spec, sort);
            return new PageImpl<>(results, Pageable.unpaged(), results.size());
        }
        Pageable newPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return this.findAll(spec, newPageable);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    @Async
    @ActiveReflection
    public void saveAllCustomer(Collection<Customer> customers) {
        customerCount.set(customerCount.get() + this.saveAll(customers).size());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    @Async
    @ActiveReflection
    public void deleteAllCustomer(Collection<Customer> customers) {
        this.deleteAllInBatch(customers);
    }

    @Override
    @PersistenceContext
    @ActiveReflection
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
