package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.EnableFullTextSearch;
import com.bachlinh.order.annotation.FullTextField;
import com.bachlinh.order.annotation.Label;
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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.lang.NonNull;
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
                @Index(name = "idx_customer_phone", columnList = "PHONE_NUMBER"),
                @Index(name = "idx_customer_email", columnList = "EMAIL", unique = true)
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

    @OneToOne(optional = false, mappedBy = "customer")
    private Cart cart;

    @OneToOne(optional = false, mappedBy = "customer", cascade = CascadeType.ALL)
    private RefreshToken refreshToken;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_MEDIA_ID")
    private CustomerMedia customerMedia;

    @OneToOne(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private EmailTrash emailTrash;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "TEMPORARY_TOKEN_ID", updatable = false)
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
}
