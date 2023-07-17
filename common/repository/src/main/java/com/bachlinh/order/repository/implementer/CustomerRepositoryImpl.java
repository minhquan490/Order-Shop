package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Customer_;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.repository.query.Join;
import com.bachlinh.order.repository.query.Operator;
import com.bachlinh.order.repository.query.QueryExtractor;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

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
    public Customer getCustomer(@NonNull Collection<Where> wheres, @NonNull Collection<Join> joins) {
        Specification<Customer> spec = Specification.where((root, query, criteriaBuilder) -> {
            var extractor = new QueryExtractor(criteriaBuilder, query, root);
            extractor.join(joins.toArray(new Join[0]));
            extractor.where(wheres.toArray(new Where[0]));
            return extractor.extract();
        });
        return findOne(spec).orElse(null);
    }

    @Override
    public Collection<Customer> getCustomers(@NonNull Collection<Where> wheres, @NonNull Collection<Join> joins, @Nullable Pageable pageable, @Nullable Sort sort) {
        Specification<Customer> spec = Specification.where((root, query, criteriaBuilder) -> {
            var extractor = new QueryExtractor(criteriaBuilder, query, root);
            extractor.where(wheres.toArray(new Where[0]));
            extractor.join(joins.toArray(new Join[0]));
            return extractor.extract();
        });
        if (pageable == null && sort == null) {
            return findAll(spec);
        }
        if (pageable != null && sort == null) {
            return findAll(spec, pageable).toList();
        }
        if (pageable == null) {
            return findAll(spec, sort);
        }
        Pageable newPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return findAll(spec, newPageable).toList();
    }

    @Override
    public Collection<Customer> getCustomerByIds(Collection<String> ids) {
        return findAllById(ids);
    }

    @Override
    public Customer getCustomerById(String id, boolean useJoin) {
        var condition = Where.builder().attribute(Customer_.ID).value(id).operator(Operator.EQ).build();
        Collection<Join> joins;
        if (useJoin) {
            joins = new ArrayList<>(2);
            joins.add(Join.builder().attribute(Customer_.REFRESH_TOKEN).type(JoinType.INNER).build());
            joins.add(Join.builder().attribute(Customer_.ADDRESSES).type(JoinType.LEFT).build());
        } else {
            joins = new ArrayList<>(0);
        }
        return getCustomer(Collections.singletonList(condition), joins);
    }

    @Override
    public Customer getCustomerByUsername(String username) {
        var usernameCondition = Where.builder().attribute(Customer_.USERNAME).value(username).operator(Operator.EQ).build();
        var enableCondition = Where.builder().attribute(Customer_.ENABLED).value(true).operator(Operator.EQ).build();
        return getCustomer(Arrays.asList(usernameCondition, enableCondition), Collections.emptyList());
    }

    @Override
    public Customer getCustomerByEmail(String email) {
        var condition = Where.builder().attribute(Customer_.EMAIL).value(email).operator(Operator.EQ).build();
        return getCustomer(Collections.singletonList(condition), Collections.emptyList());
    }

    @Override
    public Customer getCustomerByPhone(String phone) {
        var condition = Where.builder().attribute(Customer_.PHONE_NUMBER).value(phone).operator(Operator.EQ).build();
        return getCustomer(Collections.singletonList(condition), Collections.emptyList());
    }

    @Override
    public Customer getCustomerUseJoin(Object customerId, Collection<Join> joins) {
        var condition = Where.builder().attribute(Customer_.ID).value(customerId).operator(Operator.EQ).build();
        return getCustomer(Collections.singletonList(condition), joins);
    }

    @Override
    public long countCustomer() {
        return customerCount.get();
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
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
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    @ActiveReflection
    public Customer saveCustomer(@NonNull Customer customer) {
        var result = this.save(customer);
        customerCount.set(customerCount.get() - 1);
        return result;
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public Customer updateCustomer(@NonNull Customer customer) {
        return this.save(customer);
    }

    @Override
    public void updateCustomers(Collection<Customer> customers) {
        saveAll(customers);
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
    @PersistenceContext
    @ActiveReflection
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }

    @Override
    public <T> List<T> executeNativeQuery(String query, Map<String, Object> attributes, Class<T> receiverType) {
        var typedQuery = getEntityManager().createQuery(query, receiverType);
        attributes.forEach(typedQuery::setParameter);
        return typedQuery.getResultList();
    }
}
