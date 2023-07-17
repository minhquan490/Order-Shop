package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.EnableFullTextSearch;
import com.bachlinh.order.annotation.FullTextField;
import com.bachlinh.order.annotation.Label;
import com.bachlinh.order.annotation.Trigger;
import com.bachlinh.order.annotation.Validator;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
@Trigger(triggers = {"com.bachlinh.order.trigger.internal.CustomerIndexTrigger", "com.bachlinh.order.trigger.internal.CustomerInfoChangeHistoryTrigger"})
@Validator(validators = "com.bachlinh.order.validate.validator.internal.CustomerValidator")
@ActiveReflection
@NoArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__(@ActiveReflection))
@Getter
public class Customer extends AbstractEntity implements UserDetails {

    @Id
    @Column(name = "ID", updatable = false, nullable = false, columnDefinition = "varchar(32)")
    private String id;

    @Column(name = "USER_NAME", unique = true, nullable = false, length = 32, columnDefinition = "nvarchar")
    @FullTextField
    @ActiveReflection
    private String username;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "FIRST_NAME", nullable = false, columnDefinition = "nvarchar(36)")
    @FullTextField
    @ActiveReflection
    private String firstName;

    @Column(name = "LAST_NAME", nullable = false, columnDefinition = "nvarchar(36)")
    @FullTextField
    @ActiveReflection
    private String lastName;

    @Column(name = "PHONE_NUMBER", nullable = false, unique = true, length = 10)
    @FullTextField
    @ActiveReflection
    private String phoneNumber;

    @Column(name = "EMAIL", nullable = false, unique = true, length = 32, columnDefinition = "nvarchar")
    @FullTextField
    @ActiveReflection
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

    @OneToOne(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private EmailTrash emailTrash;

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

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(this.role));
    }

    public Collection<String> getAddressString() {
        return this.getAddresses().stream().map(a -> String.join(",", a.getValue(), a.getCity(), a.getCountry())).toList();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer customer)) return false;
        return isActivated() == customer.isActivated() && isAccountNonExpired() == customer.isAccountNonExpired() && isAccountNonLocked() == customer.isAccountNonLocked() && isCredentialsNonExpired() == customer.isCredentialsNonExpired() && isEnabled() == customer.isEnabled() && Objects.equal(getId(), customer.getId()) && Objects.equal(getUsername(), customer.getUsername()) && Objects.equal(getPassword(), customer.getPassword()) && Objects.equal(getFirstName(), customer.getFirstName()) && Objects.equal(getLastName(), customer.getLastName()) && Objects.equal(getPhoneNumber(), customer.getPhoneNumber()) && Objects.equal(getEmail(), customer.getEmail()) && Objects.equal(getGender(), customer.getGender()) && Objects.equal(getRole(), customer.getRole()) && Objects.equal(getOrderPoint(), customer.getOrderPoint()) && Objects.equal(getCart(), customer.getCart()) && Objects.equal(getRefreshToken(), customer.getRefreshToken()) && Objects.equal(getCustomerMedia(), customer.getCustomerMedia()) && Objects.equal(getEmailTrash(), customer.getEmailTrash()) && Objects.equal(getAddresses(), customer.getAddresses()) && Objects.equal(getOrders(), customer.getOrders()) && Objects.equal(getHistories(), customer.getHistories()) && Objects.equal(getAssignedVouchers(), customer.getAssignedVouchers());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId(), getUsername(), getPassword(), getFirstName(), getLastName(), getPhoneNumber(), getEmail(), getGender(), getRole(), getOrderPoint(), isActivated(), isAccountNonExpired(), isAccountNonLocked(), isCredentialsNonExpired(), isEnabled(), getCart(), getRefreshToken(), getCustomerMedia(), getEmailTrash(), getAddresses(), getOrders(), getHistories(), getAssignedVouchers());
    }
}
