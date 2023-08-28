package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.EnableFullTextSearch;
import com.bachlinh.order.annotation.FullTextField;
import com.bachlinh.order.annotation.Label;
import com.bachlinh.order.entity.EntityMapper;
import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import jakarta.persistence.Tuple;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

@Entity
@Label("CSR-")
@Table(
        name = "CUSTOMER",
        indexes = {
                @Index(name = "idx_customer_username", columnList = "USER_NAME", unique = true),
                @Index(name = "idx_customer_phone", columnList = "PHONE_NUMBER"),
                @Index(name = "idx_customer_email", columnList = "EMAIL", unique = true),
                @Index(name = "idx_customer_media", columnList = "CUSTOMER_MEDIA_ID"),
                @Index(name = "idx_customer_temporary_token", columnList = "TEMPORARY_TOKEN_ID")
        }
)
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "customer")
@EnableFullTextSearch
@ActiveReflection
@NoArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__(@ActiveReflection))
@Getter
@DynamicUpdate
@EqualsAndHashCode(callSuper = true)
public class Customer extends AbstractEntity<String> implements UserDetails {

    @Id
    @Column(name = "ID", updatable = false, nullable = false, columnDefinition = "varchar(32)", unique = true)
    @NonNull
    private String id;

    @Column(name = "USER_NAME", unique = true, nullable = false, columnDefinition = "nvarchar(32)")
    @FullTextField
    @ActiveReflection
    private String username;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "FIRST_NAME", columnDefinition = "nvarchar(36)")
    @FullTextField
    @ActiveReflection
    private String firstName;

    @Column(name = "LAST_NAME", columnDefinition = "nvarchar(36)")
    @FullTextField
    @ActiveReflection
    private String lastName;

    @Column(name = "PHONE_NUMBER", length = 10)
    @FullTextField
    @ActiveReflection
    private String phoneNumber;

    @Column(name = "EMAIL", unique = true, columnDefinition = "nvarchar(32)")
    @FullTextField
    @ActiveReflection
    private String email;

    @Column(name = "GENDER", length = 8)
    private String gender;

    @Column(name = "ROLE", nullable = false, length = 10)
    private String role;

    @Column(name = "ORDER_POINT", nullable = false)
    private Integer orderPoint;

    @Column(name = "ACTIVATED", nullable = false, columnDefinition = "bit")
    private boolean activated = false;

    @Column(name = "ACCOUNT_NON_EXPIRED", nullable = false, columnDefinition = "bit")
    private boolean accountNonExpired = true;

    @Column(name = "ACCOUNT_NON_LOCKED", nullable = false, columnDefinition = "bit")
    private boolean accountNonLocked = true;

    @Column(name = "CREDENTIALS_NON_EXPIRED", nullable = false, columnDefinition = "bit")
    private boolean credentialsNonExpired = true;

    @Column(name = "ENABLED", nullable = false, columnDefinition = "bit")
    private boolean enabled = true;

    @OneToOne(mappedBy = "customer", fetch = FetchType.LAZY)
    @Fetch(FetchMode.JOIN)
    @EqualsAndHashCode.Exclude
    private Cart cart;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Fetch(FetchMode.JOIN)
    @EqualsAndHashCode.Exclude
    private RefreshToken refreshToken;

    @OneToOne(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Fetch(FetchMode.JOIN)
    @EqualsAndHashCode.Exclude
    private EmailTrash emailTrash;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_MEDIA_ID")
    @Fetch(FetchMode.JOIN)
    @EqualsAndHashCode.Exclude
    private CustomerMedia customerMedia;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "TEMPORARY_TOKEN_ID", updatable = false)
    @Fetch(FetchMode.JOIN)
    @EqualsAndHashCode.Exclude
    private TemporaryToken temporaryToken;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customer", orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    private Set<Address> addresses = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customer", orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    private Set<Order> orders = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customer", orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    private Set<CustomerAccessHistory> histories = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(
            name = "USER_ASSIGNMENT",
            joinColumns = @JoinColumn(name = "VOUCHER_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "CUSTOMER_ID", referencedColumnName = "ID"),
            indexes = {
                    @Index(name = "idx_user_assignment", columnList = "CUSTOMER_ID, VOUCHER_ID")
            }
    )
    @EqualsAndHashCode.Exclude
    private Set<Voucher> assignedVouchers = new HashSet<>();

    public String getPicture() {
        return customerMedia.getUrl();
    }

    @ActiveReflection
    public void setId(Object id) {
        if (id instanceof String casted) {
            this.id = casted;
            return;
        }
        throw new PersistenceException("Id of customer is only string");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<String>> U map(Tuple resultSet) {
        return (U) getMapper().map(resultSet);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<String>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        if (entities.isEmpty()) {
            return Collections.emptyList();
        } else {
            Deque<Customer> results = new LinkedList<>();
            Customer first = null;
            for (var entity : entities) {
                if (first == null) {
                    first = (Customer) entity;
                } else {
                    Customer casted = (Customer) entity;
                    if (casted.getAddresses().isEmpty() && casted.getOrders().isEmpty() && casted.getHistories().isEmpty() && casted.getAssignedVouchers().isEmpty()) {
                        results.add(casted);
                    } else {
                        first.getAddresses().addAll(casted.getAddresses());
                        first.getOrders().addAll(casted.getOrders());
                        first.getHistories().addAll(casted.getHistories());
                        first.getAssignedVouchers().addAll(casted.getAssignedVouchers());
                    }
                }
            }
            results.addFirst(first);
            return (Collection<U>) results;
        }
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(this.role));
    }

    public Collection<String> getAddressString() {
        return this.getAddresses().stream().map(a -> {
            StringBuilder addressBuilder = new StringBuilder();
            if (a.getValue() != null) {
                addressBuilder.append(a.getValue());
            }
            if (a.getCity() != null) {
                addressBuilder.append(a.getCity());
            }
            if (a.getCountry() != null) {
                addressBuilder.append(a.getCountry());
            }
            return addressBuilder.toString();
        }).toList();
    }

    @ActiveReflection
    public void setUsername(String username) {
        if (this.username != null && !this.username.equals(username)) {
            trackUpdatedField("USER_NAME", this.username);
        }
        this.username = username;
    }

    @ActiveReflection
    public void setPassword(String password) {
        if (this.password != null && !this.password.equals(password)) {
            trackUpdatedField("PASSWORD", this.password);
        }
        this.password = password;
    }

    @ActiveReflection
    public void setFirstName(String firstName) {
        if (this.firstName != null && !this.firstName.equals(firstName)) {
            trackUpdatedField("FIRST_NAME", this.firstName);
        }
        this.firstName = firstName;
    }

    @ActiveReflection
    public void setLastName(String lastName) {
        if (this.lastName != null && !this.lastName.equals(lastName)) {
            trackUpdatedField("LAST_NAME", this.lastName);
        }
        this.lastName = lastName;
    }

    @ActiveReflection
    public void setPhoneNumber(String phoneNumber) {
        if (this.phoneNumber != null && !this.phoneNumber.equals(phoneNumber)) {
            trackUpdatedField("PHONE_NAME", this.phoneNumber);
        }
        this.phoneNumber = phoneNumber;
    }

    @ActiveReflection
    public void setEmail(String email) {
        if (this.email != null && !this.email.equals(email)) {
            trackUpdatedField("EMAIL", this.email);
        }
        this.email = email;
    }

    @ActiveReflection
    public void setGender(String gender) {
        if (this.gender != null && !this.gender.equals(gender)) {
            trackUpdatedField("GENDER", this.gender);
        }
        this.gender = gender;
    }

    @ActiveReflection
    public void setRole(String role) {
        if (this.role != null && !this.role.equals(role)) {
            trackUpdatedField("ROLE", this.role);
        }
        this.role = role;
    }

    @ActiveReflection
    public void setOrderPoint(Integer orderPoint) {
        if (this.orderPoint != null && !this.orderPoint.equals(orderPoint)) {
            trackUpdatedField("ORDER_POINT", this.orderPoint.toString());
        }
        this.orderPoint = orderPoint;
    }

    @ActiveReflection
    public void setActivated(boolean activated) {
        if (activated != this.activated) {
            trackUpdatedField("ACTIVATED", String.valueOf(this.activated));
        }
        this.activated = activated;
    }

    @ActiveReflection
    public void setAccountNonExpired(boolean accountNonExpired) {
        if (accountNonExpired != this.accountNonExpired) {
            trackUpdatedField("ACCOUNT_NON_EXPIRED", String.valueOf(this.accountNonExpired));
        }
        this.accountNonExpired = accountNonExpired;
    }

    @ActiveReflection
    public void setAccountNonLocked(boolean accountNonLocked) {
        if (accountNonLocked != this.accountNonLocked) {
            trackUpdatedField("ACCOUNT_NON_LOCKED", String.valueOf(this.accountNonLocked));
        }
        this.accountNonLocked = accountNonLocked;
    }

    @ActiveReflection
    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        if (credentialsNonExpired != this.credentialsNonExpired) {
            trackUpdatedField("CREDENTIALS_NON_EXPIRED", String.valueOf(this.credentialsNonExpired));
        }
        this.credentialsNonExpired = credentialsNonExpired;
    }

    @ActiveReflection
    public void setEnabled(boolean enabled) {
        if (enabled != this.enabled) {
            trackUpdatedField("ENABLED", String.valueOf(this.enabled));
        }
        this.enabled = enabled;
    }

    @ActiveReflection
    public void setCart(Cart cart) {
        this.cart = cart;
    }

    @ActiveReflection
    public void setRefreshToken(RefreshToken refreshToken) {
        this.refreshToken = refreshToken;
    }

    @ActiveReflection
    public void setCustomerMedia(CustomerMedia customerMedia) {
        this.customerMedia = customerMedia;
    }

    @ActiveReflection
    public void setAddresses(Set<Address> addresses) {
        this.addresses = addresses;
    }

    @ActiveReflection
    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    @ActiveReflection
    public void setHistories(Set<CustomerAccessHistory> histories) {
        this.histories = histories;
    }

    @ActiveReflection
    public void setAssignedVouchers(Set<Voucher> assignedVouchers) {
        this.assignedVouchers = assignedVouchers;
    }

    @ActiveReflection
    public void setEmailTrash(EmailTrash emailTrash) {
        this.emailTrash = emailTrash;
    }

    @ActiveReflection
    public void setTemporaryToken(TemporaryToken temporaryToken) {
        this.temporaryToken = temporaryToken;
    }

    public static EntityMapper<Customer> getMapper() {
        return new CustomerMapper();
    }

    private static class CustomerMapper implements EntityMapper<Customer> {

        @Override
        public Customer map(Tuple resultSet) {
            Queue<MappingObject> mappingObjectQueue = new Customer().parseTuple(resultSet);
            return this.map(mappingObjectQueue);
        }

        @Override
        public Customer map(Queue<MappingObject> resultSet) {
            MappingObject hook;
            Customer result = new Customer();
            while (!resultSet.isEmpty()) {
                hook = resultSet.peek();
                if (hook.columnName().split("\\.")[0].equals("CUSTOMER")) {
                    hook = resultSet.poll();
                    setData(result, hook);
                } else {
                    break;
                }
            }
            if (!resultSet.isEmpty()) {
                assignCart(resultSet, result);
            }
            if (!resultSet.isEmpty()) {
                assignRefreshToken(resultSet, result);
            }
            if (!resultSet.isEmpty()) {
                assignEmailTrash(resultSet, result);
            }
            if (!resultSet.isEmpty()) {
                assignCustomerMedia(resultSet, result);
            }
            if (!resultSet.isEmpty()) {
                assignTemporaryToken(resultSet, result);
            }
            if (!resultSet.isEmpty()) {
                assignAddress(resultSet, result);
            }
            if (!resultSet.isEmpty()) {
                assignOrders(resultSet, result);
            }
            if (!resultSet.isEmpty()) {
                assignHistories(resultSet, result);
            }
            if (!resultSet.isEmpty()) {
                assignVouchers(resultSet, result);
            }
            return result;
        }

        @Override
        public boolean canMap(Collection<MappingObject> testTarget) {
            return testTarget.stream().anyMatch(mappingObject -> {
                String name = mappingObject.columnName();
                return name.split("\\.")[0].equals("CUSTOMER");
            });
        }

        private void setData(Customer target, MappingObject mappingObject) {
            if (mappingObject.value() == null) {
                return;
            }
            switch (mappingObject.columnName()) {
                case "CUSTOMER.ID" -> target.setId(mappingObject.value());
                case "CUSTOMER.USER_NAME" -> target.setUsername((String) mappingObject.value());
                case "CUSTOMER.PASSWORD" -> target.setPassword((String) mappingObject.value());
                case "CUSTOMER.FIRST_NAME" -> target.setFirstName((String) mappingObject.value());
                case "CUSTOMER.LAST_NAME" -> target.setLastName((String) mappingObject.value());
                case "CUSTOMER.PHONE_NUMBER" -> target.setPhoneNumber((String) mappingObject.value());
                case "CUSTOMER.EMAIL" -> target.setEmail((String) mappingObject.value());
                case "CUSTOMER.GENDER" -> target.setGender((String) mappingObject.value());
                case "CUSTOMER.ROLE" -> target.setRole((String) mappingObject.value());
                case "CUSTOMER.ORDER_POINT" -> target.setOrderPoint((Integer) mappingObject.value());
                case "CUSTOMER.ACTIVATED" -> target.setActivated((Boolean) mappingObject.value());
                case "CUSTOMER.ACCOUNT_NON_EXPIRED" -> target.setAccountNonExpired((Boolean) mappingObject.value());
                case "CUSTOMER.ACCOUNT_NON_LOCKED" -> target.setAccountNonLocked((Boolean) mappingObject.value());
                case "CUSTOMER.CREDENTIALS_NON_EXPIRED" ->
                        target.setCredentialsNonExpired((Boolean) mappingObject.value());
                case "CUSTOMER.ENABLED" -> target.setEnabled((Boolean) mappingObject.value());
                case "CUSTOMER.CREATED_BY" -> target.setCreatedBy((String) mappingObject.value());
                case "CUSTOMER.MODIFIED_BY" -> target.setModifiedBy((String) mappingObject.value());
                case "CUSTOMER.CREATED_DATE" -> target.setCreatedDate((Timestamp) mappingObject.value());
                case "CUSTOMER.MODIFIED_DATE" -> target.setModifiedDate((Timestamp) mappingObject.value());
                default -> {/* Do nothing */}
            }
        }

        private void assignRefreshToken(Queue<MappingObject> resultSet, Customer result) {
            var mapper = RefreshToken.getMapper();
            var refreshToken = mapper.map(resultSet);
            result.setRefreshToken(refreshToken);
            refreshToken.setCustomer(result);
        }

        private void assignCart(Queue<MappingObject> resultSet, Customer result) {
            var mapper = Cart.getMapper();
            if (mapper.canMap(resultSet)) {
                var cart = mapper.map(resultSet);
                result.setCart(cart);
                cart.setCustomer(result);
            }
        }

        private void assignEmailTrash(Queue<MappingObject> resultSet, Customer result) {
            var mapper = EmailTrash.getMapper();
            if (mapper.canMap(resultSet)) {
                var emailTrash = mapper.map(resultSet);
                result.setEmailTrash(emailTrash);
                emailTrash.setCustomer(result);
            }
        }

        private void assignCustomerMedia(Queue<MappingObject> resultSet, Customer result) {
            var mapper = CustomerMedia.getMapper();
            if (mapper.canMap(resultSet)) {
                var customerMedia = mapper.map(resultSet);
                result.setCustomerMedia(customerMedia);
                customerMedia.setCustomer(result);
            }
        }

        private void assignTemporaryToken(Queue<MappingObject> resultSet, Customer result) {
            var mapper = TemporaryToken.getMapper();
            if (mapper.canMap(resultSet)) {
                var temporaryToken = mapper.map(resultSet);
                result.setTemporaryToken(temporaryToken);
                temporaryToken.setAssignCustomer(result);
            }
        }

        private void assignAddress(Queue<MappingObject> resultSet, Customer result) {
            var mapper = Address.getMapper();
            Set<Address> addressSet = new LinkedHashSet<>();
            while (!resultSet.isEmpty()) {
                MappingObject hook = resultSet.peek();
                if (hook.columnName().split("\\.")[0].equals("ADDRESS")) {
                    var address = mapper.map(resultSet);
                    address.setCustomer(result);
                    addressSet.add(address);
                } else {
                    break;
                }
            }
            result.setAddresses(addressSet);
        }

        private void assignOrders(Queue<MappingObject> resultSet, Customer result) {
            var mapper = Order.getMapper();
            Set<Order> orderSet = new LinkedHashSet<>();
            while (!resultSet.isEmpty()) {
                MappingObject hook = resultSet.peek();
                if (hook.columnName().split("\\.")[0].equals("ORDER")) {
                    var order = mapper.map(resultSet);
                    order.setCustomer(result);
                    orderSet.add(order);
                } else {
                    break;
                }
            }
            result.setOrders(orderSet);
        }

        private void assignHistories(Queue<MappingObject> resultSet, Customer result) {
            var mapper = CustomerAccessHistory.getMapper();
            Set<CustomerAccessHistory> historySet = new LinkedHashSet<>();
            while (!resultSet.isEmpty()) {
                MappingObject hook = resultSet.peek();
                if (hook.columnName().split("\\.")[0].equals("CUSTOMER_ACCESS_HISTORY")) {
                    var customerAccessHistory = mapper.map(resultSet);
                    customerAccessHistory.setCustomer(result);
                    historySet.add(customerAccessHistory);
                } else {
                    break;
                }
            }
            result.setHistories(historySet);
        }

        private void assignVouchers(Queue<MappingObject> resultSet, Customer result) {
            var mapper = Voucher.getMapper();
            Set<Voucher> voucherSet = new LinkedHashSet<>();
            while (!resultSet.isEmpty()) {
                MappingObject hook = resultSet.peek();
                if (hook.columnName().split("\\.")[0].equals("VOUCHER")) {
                    var voucher = mapper.map(resultSet);
                    voucher.getCustomers().add(result);
                    voucherSet.add(voucher);
                } else {
                    break;
                }
            }
            result.setAssignedVouchers(voucherSet);
        }
    }
}
