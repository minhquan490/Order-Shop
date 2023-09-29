package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.EnableFullTextSearch;
import com.bachlinh.order.annotation.Formula;
import com.bachlinh.order.annotation.FullTextField;
import com.bachlinh.order.annotation.Label;
import com.bachlinh.order.annotation.QueryCache;
import com.bachlinh.order.entity.enums.Gender;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.entity.formula.CustomerEnableFormula;
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
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
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
@EnableFullTextSearch
@ActiveReflection
@QueryCache
public class Customer extends AbstractEntity<String> implements UserDetails {

    @Id
    @Column(name = "ID", updatable = false, nullable = false, columnDefinition = "varchar(32)", unique = true)
    private String id;

    @Column(name = "USER_NAME", unique = true, nullable = false, columnDefinition = "nvarchar(32)")
    @FullTextField
    @ActiveReflection
    private String username;

    @Column(name = "PASSWORD", nullable = false)
    @Formula(processors = CustomerEnableFormula.class)
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

    @Column(name = "EMAIL", unique = true, columnDefinition = "nvarchar(266)")
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
    private Boolean activated;

    @Column(name = "ACCOUNT_NON_EXPIRED", nullable = false, columnDefinition = "bit")
    private Boolean accountNonExpired;

    @Column(name = "ACCOUNT_NON_LOCKED", nullable = false, columnDefinition = "bit")
    private Boolean accountNonLocked;

    @Column(name = "CREDENTIALS_NON_EXPIRED", nullable = false, columnDefinition = "bit")
    private Boolean credentialsNonExpired;

    @Column(name = "ENABLED", nullable = false, columnDefinition = "bit")
    private Boolean enabled;

    @OneToOne(mappedBy = "customer", fetch = FetchType.LAZY)
    @Fetch(FetchMode.JOIN)
    private Cart cart;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Fetch(FetchMode.JOIN)
    private RefreshToken refreshToken;

    @OneToOne(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Fetch(FetchMode.JOIN)
    private EmailTrash emailTrash;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_MEDIA_ID")
    @Fetch(FetchMode.JOIN)
    private CustomerMedia customerMedia;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "TEMPORARY_TOKEN_ID", updatable = false)
    @Fetch(FetchMode.JOIN)
    private TemporaryToken temporaryToken;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customer", orphanRemoval = true)
    private Set<Address> addresses = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customer", orphanRemoval = true)
    private Set<Order> orders = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customer", orphanRemoval = true)
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
    private Set<Voucher> assignedVouchers = new HashSet<>();

    @ActiveReflection
    protected Customer() {
    }

    public String getPicture() {
        if (customerMedia == null) {
            return "";
        }
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

    @Override
    public boolean isAccountNonExpired() {
        return getAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return getAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return getCredentialsNonExpired();
    }

    public boolean isActivated() {
        return getActivated();
    }

    @Override
    public boolean isEnabled() {
        return getEnabled();
    }

    public Collection<String> getAddressString() {
        return this.getAddresses().stream().map(a -> {
            StringBuilder addressBuilder = new StringBuilder();
            if (a.getValue() != null) {
                addressBuilder.append(a.getValue());
            }
            if (a.getCity() != null) {
                addressBuilder.append(", ");
                addressBuilder.append(a.getCity());
            }
            if (a.getCountry() != null) {
                addressBuilder.append(", ");
                addressBuilder.append(a.getCountry());
            }
            return addressBuilder.toString();
        }).toList();
    }

    @ActiveReflection
    public void setUsername(String username) {
        if (this.username != null && !this.username.equals(username)) {
            trackUpdatedField("USER_NAME", this.username, username);
        }
        this.username = username;
    }

    @ActiveReflection
    public void setPassword(String password) {
        if (this.password != null && !this.password.equals(password)) {
            trackUpdatedField("PASSWORD", this.password, password);
        }
        this.password = password;
    }

    @ActiveReflection
    public void setFirstName(String firstName) {
        if (this.firstName != null && !this.firstName.equals(firstName)) {
            trackUpdatedField("FIRST_NAME", this.firstName, firstName);
        }
        this.firstName = firstName;
    }

    @ActiveReflection
    public void setLastName(String lastName) {
        if (this.lastName != null && !this.lastName.equals(lastName)) {
            trackUpdatedField("LAST_NAME", this.lastName, lastName);
        }
        this.lastName = lastName;
    }

    @ActiveReflection
    public void setPhoneNumber(String phoneNumber) {
        if (this.phoneNumber != null && !this.phoneNumber.equals(phoneNumber)) {
            trackUpdatedField("PHONE_NUMBER", this.phoneNumber, phoneNumber);
        }
        this.phoneNumber = phoneNumber;
    }

    @ActiveReflection
    public void setEmail(String email) {
        if (this.email != null && !this.email.equals(email)) {
            trackUpdatedField("EMAIL", this.email, email);
        }
        this.email = email;
    }

    @ActiveReflection
    public void setGender(String gender) {
        Gender g = Gender.of(gender);
        if (this.gender != null && g != null && !this.gender.equals(g.name())) {
            trackUpdatedField("GENDER", this.gender, g.name());
        }
        this.gender = gender;
    }

    @ActiveReflection
    public void setRole(String role) {
        Role r = Role.of(Objects.requireNonNull(role).toUpperCase());
        if (this.role != null && r != null && !r.name().equals(role.toUpperCase())) {
            trackUpdatedField("ROLE", this.role, role);
        }
        this.role = role;
    }

    @ActiveReflection
    public void setOrderPoint(Integer orderPoint) {
        if (this.orderPoint != null && !this.orderPoint.equals(orderPoint)) {
            trackUpdatedField("ORDER_POINT", this.orderPoint, orderPoint);
        }
        this.orderPoint = orderPoint;
    }

    @ActiveReflection
    public void setActivated(Boolean activated) {
        if (this.activated != null && !Objects.equals(activated, this.activated)) {
            trackUpdatedField("ACTIVATED", this.activated, activated);
        }
        this.activated = activated;
    }

    @ActiveReflection
    public void setAccountNonExpired(Boolean accountNonExpired) {
        if (this.accountNonExpired != null && !Objects.equals(accountNonExpired, this.accountNonExpired)) {
            trackUpdatedField("ACCOUNT_NON_EXPIRED", this.accountNonExpired, accountNonExpired);
        }
        this.accountNonExpired = accountNonExpired;
    }

    @ActiveReflection
    public void setAccountNonLocked(Boolean accountNonLocked) {
        if (this.accountNonLocked != null && !Objects.equals(accountNonLocked, this.accountNonLocked)) {
            trackUpdatedField("ACCOUNT_NON_LOCKED", this.accountNonLocked, accountNonLocked);
        }
        this.accountNonLocked = accountNonLocked;
    }

    @ActiveReflection
    public void setCredentialsNonExpired(Boolean credentialsNonExpired) {
        if (this.credentialsNonExpired != null && !Objects.equals(credentialsNonExpired, this.credentialsNonExpired)) {
            trackUpdatedField("CREDENTIALS_NON_EXPIRED", this.credentialsNonExpired, credentialsNonExpired);
        }
        this.credentialsNonExpired = credentialsNonExpired;
    }

    @ActiveReflection
    public void setEnabled(Boolean enabled) {
        if (this.enabled != null && !Objects.equals(enabled, this.enabled)) {
            trackUpdatedField("ENABLED", this.enabled, enabled);
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

    public String getId() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public String getEmail() {
        return this.email;
    }

    public String getGender() {
        return this.gender;
    }

    public String getRole() {
        return this.role;
    }

    public Integer getOrderPoint() {
        return this.orderPoint;
    }

    public Boolean getActivated() {
        return this.activated;
    }

    public Boolean getAccountNonExpired() {
        return this.accountNonExpired;
    }

    public Boolean getAccountNonLocked() {
        return this.accountNonLocked;
    }

    public Boolean getCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    public Boolean getEnabled() {
        return this.enabled;
    }

    public Cart getCart() {
        return this.cart;
    }

    public RefreshToken getRefreshToken() {
        return this.refreshToken;
    }

    public EmailTrash getEmailTrash() {
        return this.emailTrash;
    }

    public CustomerMedia getCustomerMedia() {
        return this.customerMedia;
    }

    public TemporaryToken getTemporaryToken() {
        return this.temporaryToken;
    }

    public Set<Address> getAddresses() {
        return this.addresses;
    }

    public Set<Order> getOrders() {
        return this.orders;
    }

    public Set<CustomerAccessHistory> getHistories() {
        return this.histories;
    }

    public Set<Voucher> getAssignedVouchers() {
        return this.assignedVouchers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Customer customer = (Customer) o;
        return com.google.common.base.Objects.equal(getId(), customer.getId()) && com.google.common.base.Objects.equal(getUsername(), customer.getUsername()) && com.google.common.base.Objects.equal(getPassword(), customer.getPassword()) && com.google.common.base.Objects.equal(getFirstName(), customer.getFirstName()) && com.google.common.base.Objects.equal(getLastName(), customer.getLastName()) && com.google.common.base.Objects.equal(getPhoneNumber(), customer.getPhoneNumber()) && com.google.common.base.Objects.equal(getEmail(), customer.getEmail()) && com.google.common.base.Objects.equal(getGender(), customer.getGender()) && com.google.common.base.Objects.equal(getRole(), customer.getRole()) && com.google.common.base.Objects.equal(getOrderPoint(), customer.getOrderPoint()) && com.google.common.base.Objects.equal(isActivated(), customer.isActivated()) && com.google.common.base.Objects.equal(isAccountNonExpired(), customer.isAccountNonExpired()) && com.google.common.base.Objects.equal(isAccountNonLocked(), customer.isAccountNonLocked()) && com.google.common.base.Objects.equal(isCredentialsNonExpired(), customer.isCredentialsNonExpired()) && com.google.common.base.Objects.equal(isEnabled(), customer.isEnabled());
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(super.hashCode(), getId(), getUsername(), getPassword(), getFirstName(), getLastName(), getPhoneNumber(), getEmail(), getGender(), getRole(), getOrderPoint(), isActivated(), isAccountNonExpired(), isAccountNonLocked(), isCredentialsNonExpired(), isEnabled());
    }
}
