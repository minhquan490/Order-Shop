package com.bachlinh.order.web.repository.implementer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.JoinType;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DependenciesInitialize;
import com.bachlinh.order.core.annotation.RepositoryComponent;
import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.core.exception.system.common.CriticalException;
import com.bachlinh.order.core.function.VoidCallback;
import com.bachlinh.order.entity.model.Address;
import com.bachlinh.order.entity.model.Address_;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.CustomerAccessHistory;
import com.bachlinh.order.entity.model.CustomerAccessHistory_;
import com.bachlinh.order.entity.model.CustomerMedia;
import com.bachlinh.order.entity.model.CustomerMedia_;
import com.bachlinh.order.entity.model.Customer_;
import com.bachlinh.order.entity.model.Order;
import com.bachlinh.order.entity.model.OrderStatus;
import com.bachlinh.order.entity.model.OrderStatus_;
import com.bachlinh.order.entity.model.Order_;
import com.bachlinh.order.entity.model.Voucher;
import com.bachlinh.order.entity.model.Voucher_;
import com.bachlinh.order.web.repository.spi.AbstractRepository;
import com.bachlinh.order.repository.RepositoryBase;
import com.bachlinh.order.repository.query.Join;
import com.bachlinh.order.repository.query.Operation;
import com.bachlinh.order.repository.query.OrderBy;
import com.bachlinh.order.repository.query.Select;
import com.bachlinh.order.repository.query.SqlBuilder;
import com.bachlinh.order.repository.query.SqlJoin;
import com.bachlinh.order.repository.query.SqlSelect;
import com.bachlinh.order.repository.query.SqlWhere;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.entity.transaction.spi.EntityTransactionManager;
import com.bachlinh.order.repository.utils.QueryUtils;
import com.bachlinh.order.web.repository.spi.CustomerRepository;
import com.bachlinh.order.web.repository.spi.UserAssignmentRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

@RepositoryComponent
@ActiveReflection
public class CustomerRepositoryImpl extends AbstractRepository<String, Customer> implements CustomerRepository, UserAssignmentRepository, RepositoryBase {
    private final AtomicLong customerCount = new AtomicLong(0);

    @DependenciesInitialize
    @ActiveReflection
    public CustomerRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(Customer.class, containerResolver.getDependenciesResolver());
    }

    @Override
    public Collection<Customer> getCustomerByIds(Collection<String> ids) {
        Where customerIdWhere = Where.builder().attribute(Customer_.ID).value(ids.toArray()).operation(Operation.IN).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Customer.class);
        SqlWhere sqlWhere = sqlSelect.where(customerIdWhere);
        String query = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return this.getResultList(query, attributes, Customer.class);
    }

    @Override
    public Customer getCustomerBasicInformation(String customerId) {
        Join avatarJoin = Join.builder().attribute(Customer_.CUSTOMER_MEDIA).type(JoinType.LEFT).build();
        Select idSelect = Select.builder().column(Customer_.ID).build();
        Select firstNameSelect = Select.builder().column(Customer_.FIRST_NAME).build();
        Select lastNameSelect = Select.builder().column(Customer_.LAST_NAME).build();
        Select roleSelect = Select.builder().column(Customer_.ROLE).build();
        Select mediaSelect = Select.builder().column(Customer_.CUSTOMER_MEDIA).build();
        Where customerIdWhere = Where.builder().attribute(Customer_.ID).value(customerId).operation(Operation.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Customer.class);
        sqlSelect.select(idSelect).select(firstNameSelect).select(lastNameSelect).select(roleSelect).select(mediaSelect);
        SqlJoin sqlJoin = sqlSelect.join(avatarJoin);
        SqlWhere sqlWhere = sqlJoin.where(customerIdWhere);
        return getCustomer(sqlWhere, false);
    }

    @Override
    public Customer getCustomerForRefreshTokenGeneration(String customerId) {
        Select idSelect = Select.builder().column(Customer_.ID).build();
        Where idWhere = Where.builder().attribute(Customer_.ID).value(customerId).operation(Operation.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Customer.class).select(idSelect);
        SqlWhere sqlWhere = sqlSelect.where(idWhere);
        return getCustomer(sqlWhere, false);
    }

    @Override
    public Customer getCustomerForAuthentication(String customerId) {
        Select idSelect = Select.builder().column(Customer_.ID).build();
        Select usernameSelect = Select.builder().column(Customer_.USERNAME).build();
        Select firstNameSelect = Select.builder().column(Customer_.FIRST_NAME).build();
        Select lastNameSelect = Select.builder().column(Customer_.LAST_NAME).build();
        Select phoneSelect = Select.builder().column(Customer_.PHONE_NUMBER).build();
        Select emailSelect = Select.builder().column(Customer_.EMAIL).build();
        Select genderSelect = Select.builder().column(Customer_.GENDER).build();
        Select roleSelect = Select.builder().column(Customer_.ROLE).build();
        Select orderPointSelect = Select.builder().column(Customer_.ORDER_POINT).build();
        Select activatedSelect = Select.builder().column(Customer_.ACTIVATED).build();
        Select accountNonExpiredSelect = Select.builder().column(Customer_.ACCOUNT_NON_EXPIRED).build();
        Select accountNonLockedSelect = Select.builder().column(Customer_.ACCOUNT_NON_LOCKED).build();
        Select credentialsNonExpiredSelect = Select.builder().column(Customer_.CREDENTIALS_NON_EXPIRED).build();
        Select enabledSelect = Select.builder().column(Customer_.ENABLED).build();
        Where idWhere = Where.builder().attribute(Customer_.ID).value(customerId).operation(Operation.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Customer.class);
        sqlSelect.select(idSelect)
                .select(usernameSelect)
                .select(firstNameSelect)
                .select(lastNameSelect)
                .select(phoneSelect)
                .select(emailSelect)
                .select(genderSelect)
                .select(roleSelect)
                .select(orderPointSelect)
                .select(activatedSelect)
                .select(accountNonExpiredSelect)
                .select(accountNonLockedSelect)
                .select(credentialsNonExpiredSelect)
                .select(enabledSelect);
        SqlWhere sqlWhere = sqlSelect.where(idWhere);
        return getCustomer(sqlWhere, false);
    }

    @Override
    public Customer getCustomerForLogin(String username) {
        var usernameCondition = Where.builder().attribute(Customer_.USERNAME).value(username).operation(Operation.EQ).build();
        var idSelect = Select.builder().column(Customer_.ID).build();
        var usernameSelect = Select.builder().column(Customer_.USERNAME).build();
        var passwordSelect = Select.builder().column(Customer_.PASSWORD).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Customer.class);
        sqlSelect.select(idSelect).select(usernameSelect).select(passwordSelect);
        SqlWhere sqlWhere = sqlSelect.where(usernameCondition);
        return getCustomer(sqlWhere, true);
    }

    @Override
    public Customer getCustomerForResetPassword(String email) {
        Select idSelect = Select.builder().column(Customer_.ID).build();
        Where emailWhere = Where.builder().attribute(Customer_.EMAIL).value(email).operation(Operation.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Customer.class);
        sqlSelect.select(idSelect);
        SqlWhere sqlWhere = sqlSelect.where(emailWhere);
        return getCustomer(sqlWhere, true);
    }

    @Override
    public Customer getCustomerForEmailSending(String customerId) {
        Select idSelect = Select.builder().column(Customer_.ID).build();
        Select emailSelect = Select.builder().column(Customer_.EMAIL).build();
        Where customerIdWhere = Where.builder().attribute(Customer_.ID).value(customerId).operation(Operation.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Customer.class);
        sqlSelect.select(idSelect).select(emailSelect);
        SqlWhere sqlWhere = sqlSelect.where(customerIdWhere);
        return getCustomer(sqlWhere, true);
    }

    @Override
    public Customer getCustomerUpdatableInfo(String customerId) {
        Select idSelect = Select.builder().column(Customer_.ID).build();
        Select usernameSelect = Select.builder().column(Customer_.USERNAME).build();
        Select firstNameSelect = Select.builder().column(Customer_.FIRST_NAME).build();
        Select lastNameSelect = Select.builder().column(Customer_.LAST_NAME).build();
        Select phoneNumberSelect = Select.builder().column(Customer_.PHONE_NUMBER).build();
        Select emailSelect = Select.builder().column(Customer_.EMAIL).build();
        Select genderSelect = Select.builder().column(Customer_.GENDER).build();
        Select roleSelect = Select.builder().column(Customer_.ROLE).build();
        Select orderPointSelect = Select.builder().column(Customer_.ORDER_POINT).build();
        Select addressValueSelect = Select.builder().column(Address_.VALUE).build();
        Select addressCitySelect = Select.builder().column(Address_.CITY).build();
        Select addressCountrySelect = Select.builder().column(Address_.COUNTRY).build();
        Join addressJoin = Join.builder().attribute(Customer_.ADDRESSES).type(JoinType.LEFT).build();
        Where customerIdWhere = Where.builder().attribute(Customer_.ID).value(customerId).operation(Operation.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Customer.class);
        sqlSelect.select(idSelect)
                .select(usernameSelect)
                .select(firstNameSelect)
                .select(lastNameSelect)
                .select(phoneNumberSelect)
                .select(emailSelect)
                .select(genderSelect)
                .select(roleSelect)
                .select(orderPointSelect)
                .select(addressValueSelect, Address.class)
                .select(addressCitySelect, Address.class)
                .select(addressCountrySelect, Address.class);
        SqlJoin sqlJoin = sqlSelect.join(addressJoin);
        SqlWhere sqlWhere = sqlJoin.where(customerIdWhere);
        return getCustomer(sqlWhere, true);
    }

    @Override
    public Customer getCustomerInfoForUpdate(String customerId) {
        Select idSelect = Select.builder().column(Customer_.ID).build();
        Select usernameSelect = Select.builder().column(Customer_.USERNAME).build();
        Select firstNameSelect = Select.builder().column(Customer_.FIRST_NAME).build();
        Select lastNameSelect = Select.builder().column(Customer_.LAST_NAME).build();
        Select phoneNumberSelect = Select.builder().column(Customer_.PHONE_NUMBER).build();
        Select emailSelect = Select.builder().column(Customer_.EMAIL).build();
        Select genderSelect = Select.builder().column(Customer_.GENDER).build();
        Select roleSelect = Select.builder().column(Customer_.ROLE).build();
        Select orderPointSelect = Select.builder().column(Customer_.ORDER_POINT).build();
        Select activatedSelect = Select.builder().column(Customer_.ACTIVATED).build();
        Select accountNonExpiredSelect = Select.builder().column(Customer_.ACCOUNT_NON_EXPIRED).build();
        Select accountNonLockedSelect = Select.builder().column(Customer_.ACCOUNT_NON_LOCKED).build();
        Select credentialsNonExpiredSelect = Select.builder().column(Customer_.CREDENTIALS_NON_EXPIRED).build();
        Select enabledSelect = Select.builder().column(Customer_.ENABLED).build();
        Where customerIdWhere = Where.builder().attribute(Customer_.ID).value(customerId).operation(Operation.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Customer.class);
        sqlSelect.select(idSelect)
                .select(usernameSelect)
                .select(firstNameSelect)
                .select(lastNameSelect)
                .select(phoneNumberSelect)
                .select(emailSelect)
                .select(genderSelect)
                .select(roleSelect)
                .select(orderPointSelect)
                .select(activatedSelect)
                .select(accountNonExpiredSelect)
                .select(accountNonLockedSelect)
                .select(credentialsNonExpiredSelect)
                .select(enabledSelect);
        SqlWhere sqlWhere = sqlSelect.where(customerIdWhere);
        return getCustomer(sqlWhere, true);
    }

    @Override
    public Customer getCustomerForDelete(String customerId) {
        Select idSelect = Select.builder().column(Customer_.ID).build();
        Where customerIdWhere = Where.builder().attribute(Customer_.ID).value(customerId).operation(Operation.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Customer.class);
        sqlSelect.select(idSelect);
        SqlWhere sqlWhere = sqlSelect.where(customerIdWhere);
        return getCustomer(sqlWhere, false);
    }

    @Override
    public Customer getFullInformation(String customerId) {
        Select idSelect = Select.builder().column(Customer_.ID).build();
        Select usernameSelect = Select.builder().column(Customer_.USERNAME).build();
        Select firstNameSelect = Select.builder().column(Customer_.FIRST_NAME).build();
        Select lastNameSelect = Select.builder().column(Customer_.LAST_NAME).build();
        Select phoneNumberSelect = Select.builder().column(Customer_.PHONE_NUMBER).build();
        Select emailSelect = Select.builder().column(Customer_.EMAIL).build();
        Select genderSelect = Select.builder().column(Customer_.GENDER).build();
        Select roleSelect = Select.builder().column(Customer_.ROLE).build();
        Select orderPointSelect = Select.builder().column(Customer_.ORDER_POINT).build();
        Select activatedSelect = Select.builder().column(Customer_.ACTIVATED).build();
        Select accountNonExpiredSelect = Select.builder().column(Customer_.ACCOUNT_NON_EXPIRED).build();
        Select accountNonLockedSelect = Select.builder().column(Customer_.ACCOUNT_NON_LOCKED).build();
        Select credentialsNonExpiredSelect = Select.builder().column(Customer_.CREDENTIALS_NON_EXPIRED).build();
        Select enabledSelect = Select.builder().column(Customer_.ENABLED).build();

        Select customerMediaIdSelect = Select.builder().column(CustomerMedia_.ID).build();
        Select customerMediaUrlSelect = Select.builder().column(CustomerMedia_.URL).build();
        Select customerMediaContentTypeSelect = Select.builder().column(CustomerMedia_.CONTENT_TYPE).build();
        Select customerMediaContentLengthSelect = Select.builder().column(CustomerMedia_.CONTENT_LENGTH).build();

        Select customerAddressIdSelect = Select.builder().column(Address_.ID).build();
        Select customerAddressValueSelect = Select.builder().column(Address_.VALUE).build();
        Select customerAddressCitySelect = Select.builder().column(Address_.CITY).build();
        Select customerAddressCountrySelect = Select.builder().column(Address_.COUNTRY).build();

        Select customerOrderIdSelect = Select.builder().column(Order_.ID).build();
        Select customerTimeOrderSelect = Select.builder().column(Order_.TIME_ORDER).build();
        Select customerOrderTransactionCodeSelect = Select.builder().column(Order_.BANK_TRANSACTION_CODE).build();
        Select orderStatusSelect = Select.builder().column(OrderStatus_.STATUS).build();

        Select customerAccessHistoryId = Select.builder().column(CustomerAccessHistory_.ID).build();
        Select customerAccessHistoryPathRequestSelect = Select.builder().column(CustomerAccessHistory_.PATH_REQUEST).build();
        Select customerAccessHistoryRequestTypeSelect = Select.builder().column(CustomerAccessHistory_.REQUEST_TYPE).build();
        Select customerAccessHistoryRequestTimeSelect = Select.builder().column(CustomerAccessHistory_.REQUEST_TIME).build();

        Select customerAssignVoucherIdSelect = Select.builder().column(Voucher_.ID).build();
        Select customerAssignVoucherNameSelect = Select.builder().column(Voucher_.NAME).build();

        Join historyJoin = Join.builder().attribute(Customer_.HISTORIES).type(JoinType.LEFT).build();
        Join voucherJoin = Join.builder().attribute(Customer_.ASSIGNED_VOUCHERS).type(JoinType.LEFT).build();
        Join orderJoin = Join.builder().attribute(Customer_.ORDERS).type(JoinType.LEFT).build();
        Join addressJoin = Join.builder().attribute(Customer_.ADDRESSES).type(JoinType.LEFT).build();
        Join mediaJoin = Join.builder().attribute(Customer_.CUSTOMER_MEDIA).type(JoinType.LEFT).build();
        Join orderStatusJoin = Join.builder().attribute(Order_.ORDER_STATUS).type(JoinType.LEFT).build();

        Where customerIdWhere = Where.builder().attribute(Customer_.ID).value(customerId).operation(Operation.EQ).build();

        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Customer.class);
        sqlSelect.select(idSelect)
                .select(usernameSelect)
                .select(firstNameSelect)
                .select(lastNameSelect)
                .select(phoneNumberSelect)
                .select(emailSelect)
                .select(genderSelect)
                .select(roleSelect)
                .select(orderPointSelect)
                .select(activatedSelect)
                .select(accountNonExpiredSelect)
                .select(accountNonLockedSelect)
                .select(credentialsNonExpiredSelect)
                .select(enabledSelect)
                .select(customerMediaIdSelect, CustomerMedia.class)
                .select(customerMediaUrlSelect, CustomerMedia.class)
                .select(customerMediaContentTypeSelect, CustomerMedia.class)
                .select(customerMediaContentLengthSelect, CustomerMedia.class)
                .select(customerAddressIdSelect, Address.class)
                .select(customerAddressValueSelect, Address.class)
                .select(customerAddressCitySelect, Address.class)
                .select(customerAddressCountrySelect, Address.class)
                .select(customerOrderIdSelect, Order.class)
                .select(customerTimeOrderSelect, Order.class)
                .select(customerOrderTransactionCodeSelect, Order.class)
                .select(orderStatusSelect, OrderStatus.class)
                .select(customerAccessHistoryId, CustomerAccessHistory.class)
                .select(customerAccessHistoryPathRequestSelect, CustomerAccessHistory.class)
                .select(customerAccessHistoryRequestTypeSelect, CustomerAccessHistory.class)
                .select(customerAccessHistoryRequestTimeSelect, CustomerAccessHistory.class)
                .select(customerAssignVoucherIdSelect, Voucher.class)
                .select(customerAssignVoucherNameSelect, Voucher.class);
        SqlJoin sqlJoin = sqlSelect.join(mediaJoin)
                .join(addressJoin)
                .join(orderJoin)
                .join(orderStatusJoin, Order.class)
                .join(historyJoin)
                .join(voucherJoin);
        SqlWhere sqlWhere = sqlJoin.where(customerIdWhere);
        return getCustomer(sqlWhere, true);
    }

    @Override
    @ActiveReflection
    public void deleteCustomer(@NonNull Customer customer) {
        if (StringUtils.hasText((CharSequence) customer.getId())) {
            deleteById(customer.getId());
        }
    }

    @Override
    @ActiveReflection
    public Customer saveCustomer(@NonNull Customer customer) {
        var result = this.save(customer);
        customerCount.set(customerCount.get() - 1);
        return result;
    }

    @Override
    public Customer updateCustomer(@NonNull Customer customer) {
        return this.save(customer);
    }

    @Override
    public void updateCustomers(Collection<Customer> customers) {
        saveAll(customers);
    }

    @Override
    public boolean isEmailExisted(String email) {
        Select idSelect = Select.builder().column(Customer_.ID).build();
        Where emailWhere = Where.builder().attribute(Customer_.EMAIL).value(email).operation(Operation.EQ).build();
        return isColumnValueExisted(idSelect, emailWhere);
    }

    @Override
    public boolean isPhoneNumberExisted(String phoneNumber) {
        Select idSelect = Select.builder().column(Customer_.ID).build();
        Where phoneNumberWhere = Where.builder().attribute(Customer_.PHONE_NUMBER).value(phoneNumber).operation(Operation.EQ).build();
        return isColumnValueExisted(idSelect, phoneNumberWhere);
    }

    @Override
    public boolean isUsernameExisted(String username) {
        Select idSelect = Select.builder().column(Customer_.ID).build();
        Where usernameWhere = Where.builder().attribute(Customer_.USERNAME).value(username).operation(Operation.EQ).build();
        return isColumnValueExisted(idSelect, usernameWhere);
    }

    @Override
    public boolean isCustomerIdExisted(String customerId) {
        Select idSelect = Select.builder().column(Customer_.ID).build();
        Where customerIdWhere = Where.builder().attribute(Customer_.ID).value(customerId).operation(Operation.EQ).build();
        return isColumnValueExisted(idSelect, customerIdWhere);
    }

    @Override
    public Page<Customer> getAll(Pageable pageable, Collection<OrderBy> orderByCollection) {
        Select idSelect = Select.builder().column(Customer_.ID).build();
        Select usernameSelect = Select.builder().column(Customer_.USERNAME).build();
        Select firstNameSelect = Select.builder().column(Customer_.FIRST_NAME).build();
        Select lastNameSelect = Select.builder().column(Customer_.LAST_NAME).build();
        Select phoneNumberSelect = Select.builder().column(Customer_.PHONE_NUMBER).build();
        Select emailSelect = Select.builder().column(Customer_.EMAIL).build();
        Select genderSelect = Select.builder().column(Customer_.GENDER).build();
        Select roleSelect = Select.builder().column(Customer_.ROLE).build();
        Select orderPointSelect = Select.builder().column(Customer_.ORDER_POINT).build();
        Select activatedSelect = Select.builder().column(Customer_.ACTIVATED).build();
        Select accountNonExpiredSelect = Select.builder().column(Customer_.ACCOUNT_NON_EXPIRED).build();
        Select accountNonLockedSelect = Select.builder().column(Customer_.ACCOUNT_NON_LOCKED).build();
        Select credentialsNonExpiredSelect = Select.builder().column(Customer_.CREDENTIALS_NON_EXPIRED).build();
        Select enabledSelect = Select.builder().column(Customer_.ENABLED).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Customer.class);
        sqlSelect.select(idSelect)
                .select(usernameSelect)
                .select(firstNameSelect)
                .select(lastNameSelect)
                .select(phoneNumberSelect)
                .select(emailSelect)
                .select(genderSelect)
                .select(roleSelect)
                .select(orderPointSelect)
                .select(activatedSelect)
                .select(accountNonExpiredSelect)
                .select(accountNonLockedSelect)
                .select(credentialsNonExpiredSelect)
                .select(enabledSelect);
        if (pageable == null && orderByCollection.isEmpty()) {
            String sql = sqlSelect.getNativeQuery();
            List<Customer> results = this.getResultList(sql, Collections.emptyMap(), Customer.class);
            return new PageImpl<>(results, Pageable.unpaged(), results.size());
        }
        if (pageable != null && orderByCollection.isEmpty()) {
            long offset = pageable.getOffset();
            long limit = pageable.getPageSize();
            sqlSelect.limit(limit).offset(offset);
            String sql = sqlSelect.getNativeQuery();
            List<Customer> results = this.getResultList(sql, Collections.emptyMap(), Customer.class);
            return new PageImpl<>(results, Pageable.unpaged(), results.size());
        }
        if (pageable == null) {
            orderByCollection.forEach(sqlSelect::orderBy);
            String sql = sqlSelect.getNativeQuery();
            List<Customer> results = this.getResultList(sql, Collections.emptyMap(), Customer.class);
            return new PageImpl<>(results, Pageable.unpaged(), results.size());
        }
        long offset = pageable.getOffset();
        long limit = pageable.getPageSize();
        sqlSelect.limit(limit).offset(offset);
        orderByCollection.forEach(sqlSelect::orderBy);
        String sql = sqlSelect.getNativeQuery();
        List<Customer> results = this.getResultList(sql, Collections.emptyMap(), Customer.class, true);
        return new PageImpl<>(results, Pageable.unpaged(), results.size());
    }

    @Override
    public void deleteUserAssignmentOfCustomer(Customer customer) {
        EntityTransactionManager<?> entityTransactionManager = getEntityFactory().getTransactionManager();
        String query = "DELETE FROM USER_ASSIGNMENT WHERE CUSTOMER_ID = :customerId";
        EntityManager entityManager = getEntityManager();

        Map<String, Object> attributes = HashMap.newHashMap(1);
        attributes.put("customerId", customer.getId());

        if (entityTransactionManager.isActualTransactionActive()) {
            try {
                entityManager.getTransaction().begin();
                doDelete(query, attributes, entityManager);
                entityManager.getTransaction().commit();
            } catch (Exception e) {
                entityManager.getTransaction().rollback();
                throw new CriticalException(e);
            }
        } else {
            doDelete(query, attributes, entityManager);
        }
    }

    @Override
    public void deleteUserAssignment(Voucher voucher) {
        EntityManager entityManager = getEntityManager();

        VoidCallback callback = (unused) -> {
            String query = "DELETE FROM USER_ASSIGNMENT WHERE VOUCHER_ID = :voucherId";

            Query q = entityManager.createQuery(query);

            q.setParameter("voucherId", voucher.getId());

            q.executeUpdate();
        };

        doInTransaction(entityManager, callback);
    }

    private void doDelete(String query, Map<String, Object> attributes, EntityManager entityManager) {
        Query q = entityManager.createQuery(query);
        attributes.forEach(q::setParameter);
        q.executeUpdate();
    }

    @Override
    public RepositoryBase getInstance(DependenciesContainerResolver containerResolver) {
        return new CustomerRepositoryImpl(containerResolver);
    }

    @Override
    public Class<?>[] getRepositoryTypes() {
        return new Class[]{CustomerRepository.class, UserAssignmentRepository.class};
    }

    @Nullable
    private Customer getCustomer(SqlWhere sqlWhere, boolean forceCache) {
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return getSingleResult(sql, attributes, Customer.class, forceCache);
    }

    private boolean isColumnValueExisted(Select idSelect, Where where) {
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Customer.class);
        sqlSelect.select(idSelect);
        SqlWhere sqlWhere = sqlSelect.where(where);
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return !this.getResultList(sql, attributes, Customer.class).isEmpty();
    }
}
