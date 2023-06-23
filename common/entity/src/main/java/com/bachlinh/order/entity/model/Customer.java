package com.bachlinh.order.entity.model;

import com.google.common.base.Objects;
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
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.EnableFullTextSearch;
import com.bachlinh.order.annotation.FullTextField;
import com.bachlinh.order.annotation.Label;
import com.bachlinh.order.annotation.Trigger;
import com.bachlinh.order.annotation.Validator;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Label("CSR-")
@Table(
        name = "CUSTOMER",
        indexes = {
                @Index(name = "idx_customer_username", columnList = "USER_NAME", unique = true),
                @Index(name = "idx_customer_phone", columnList = "PHONE_NUMBER", unique = true),
                @Index(name = "idx_customer_email", columnList = "EMAIL", unique = true)
        }
)
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "customer")
@EnableFullTextSearch
@Trigger(triggers = "com.bachlinh.order.trigger.internal.CustomerIndexTrigger")
@Validator(validators = "com.bachlinh.order.validator.internal.CustomerValidator")
@ActiveReflection
public class Customer extends AbstractEntity implements UserDetails {

    @Id
    @Column(name = "ID", updatable = false, nullable = false, columnDefinition = "varchar(32)")
    private String id;

    @Column(name = "USER_NAME", unique = true, nullable = false, length = 24, columnDefinition = "nvarchar")
    @FullTextField
    private String username;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "FIRST_NAME", nullable = false, columnDefinition = "nvarchar(36)")
    @FullTextField
    private String firstName;

    @Column(name = "LAST_NAME", nullable = false, columnDefinition = "nvarchar(36)")
    @FullTextField
    private String lastName;

    @Column(name = "PHONE_NUMBER", nullable = false, unique = true, length = 10)
    @FullTextField
    private String phoneNumber;

    @Column(name = "EMAIL", nullable = false, unique = true, length = 32, columnDefinition = "nvarchar")
    private String email;

    @Column(name = "GENDER", nullable = false, length = 8)
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

    @OneToOne(optional = false, mappedBy = "customer")
    private Cart cart;

    @OneToOne(optional = false, mappedBy = "customer", cascade = CascadeType.ALL)
    private RefreshToken refreshToken;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_MEDIA_ID")
    private CustomerMedia customerMedia;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customer", orphanRemoval = true)
    private Set<Address> addresses = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customer", orphanRemoval = true)
    private Set<Order> orders = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customer", orphanRemoval = true)
    private Set<CustomerHistory> histories = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(
            name = "USER_ASSIGNMENT",
            joinColumns = @JoinColumn(name = "VOUCHER_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "CUSTOMER_ID", referencedColumnName = "ID"),
            indexes = {
                    @Index(name = "idx_user_assignment", columnList = "CUSTOMER_ID, VOUCHER_ID")
            }
    )
    private Set<Voucher> assignedVouchers;

    @ActiveReflection
    Customer() {
    }

    @ActiveReflection
    public void setId(Object id) {
        if (id instanceof String casted) {
            this.id = casted;
            return;
        }
        throw new PersistenceException("Id of customer is only string");
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(this.role));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equal(getId(), customer.getId()) && Objects.equal(getUsername(), customer.getUsername()) && Objects.equal(getPhoneNumber(), customer.getPhoneNumber()) && Objects.equal(getEmail(), customer.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId(), getUsername(), getPhoneNumber(), getEmail());
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

    public boolean isActivated() {
        return this.activated;
    }

    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public Cart getCart() {
        return this.cart;
    }

    public RefreshToken getRefreshToken() {
        return this.refreshToken;
    }

    public CustomerMedia getCustomerMedia() {
        return customerMedia;
    }

    public Set<Address> getAddresses() {
        return this.addresses;
    }

    public Set<Order> getOrders() {
        return this.orders;
    }

    public Set<CustomerHistory> getHistories() {
        return this.histories;
    }
    
    public Set<Voucher> getAssignedVouchers() {
        return this.assignedVouchers;
    }

    @ActiveReflection
    public void setUsername(String username) {
        this.username = username;
    }

    @ActiveReflection
    public void setPassword(String password) {
        this.password = password;
    }

    @ActiveReflection
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @ActiveReflection
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @ActiveReflection
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @ActiveReflection
    public void setEmail(String email) {
        this.email = email;
    }

    @ActiveReflection
    public void setGender(String gender) {
        this.gender = gender;
    }

    @ActiveReflection
    public void setRole(String role) {
        this.role = role;
    }

    @ActiveReflection
    public void setOrderPoint(Integer orderPoint) {
        this.orderPoint = orderPoint;
    }

    @ActiveReflection
    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    @ActiveReflection
    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    @ActiveReflection
    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    @ActiveReflection
    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    @ActiveReflection
    public void setEnabled(boolean enabled) {
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
    public void setHistories(Set<CustomerHistory> histories) {
        this.histories = histories;
    }

    @ActiveReflection
    public void setAssignedVouchers(Set<Voucher> assignedVouchers) {
        this.assignedVouchers = assignedVouchers;
    }
}
