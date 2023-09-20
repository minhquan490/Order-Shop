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

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Customer)) return false;
        final Customer other = (Customer) o;
        if (!other.canEqual(this)) return false;
        if (!super.equals(o)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (!Objects.equals(this$id, other$id)) return false;
        final Object this$username = this.getUsername();
        final Object other$username = other.getUsername();
        if (!Objects.equals(this$username, other$username)) return false;
        final Object this$password = this.getPassword();
        final Object other$password = other.getPassword();
        if (!Objects.equals(this$password, other$password)) return false;
        final Object this$firstName = this.getFirstName();
        final Object other$firstName = other.getFirstName();
        if (!Objects.equals(this$firstName, other$firstName)) return false;
        final Object this$lastName = this.getLastName();
        final Object other$lastName = other.getLastName();
        if (!Objects.equals(this$lastName, other$lastName)) return false;
        final Object this$phoneNumber = this.getPhoneNumber();
        final Object other$phoneNumber = other.getPhoneNumber();
        if (!Objects.equals(this$phoneNumber, other$phoneNumber))
            return false;
        final Object this$email = this.getEmail();
        final Object other$email = other.getEmail();
        if (!Objects.equals(this$email, other$email)) return false;
        final Object this$gender = this.getGender();
        final Object other$gender = other.getGender();
        if (!Objects.equals(this$gender, other$gender)) return false;
        final Object this$role = this.getRole();
        final Object other$role = other.getRole();
        if (!Objects.equals(this$role, other$role)) return false;
        final Object this$orderPoint = this.getOrderPoint();
        final Object other$orderPoint = other.getOrderPoint();
        if (!Objects.equals(this$orderPoint, other$orderPoint))
            return false;
        final Object this$activated = this.getActivated();
        final Object other$activated = other.getActivated();
        if (!Objects.equals(this$activated, other$activated)) return false;
        final Object this$accountNonExpired = this.getAccountNonExpired();
        final Object other$accountNonExpired = other.getAccountNonExpired();
        if (!Objects.equals(this$accountNonExpired, other$accountNonExpired))
            return false;
        final Object this$accountNonLocked = this.getAccountNonLocked();
        final Object other$accountNonLocked = other.getAccountNonLocked();
        if (!Objects.equals(this$accountNonLocked, other$accountNonLocked))
            return false;
        final Object this$credentialsNonExpired = this.getCredentialsNonExpired();
        final Object other$credentialsNonExpired = other.getCredentialsNonExpired();
        if (!Objects.equals(this$credentialsNonExpired, other$credentialsNonExpired))
            return false;
        final Object this$enabled = this.getEnabled();
        final Object other$enabled = other.getEnabled();
        return Objects.equals(this$enabled, other$enabled);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Customer;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $id = this.getId();
        result = result * PRIME + $id.hashCode();
        final Object $username = this.getUsername();
        result = result * PRIME + ($username == null ? 43 : $username.hashCode());
        final Object $password = this.getPassword();
        result = result * PRIME + ($password == null ? 43 : $password.hashCode());
        final Object $firstName = this.getFirstName();
        result = result * PRIME + ($firstName == null ? 43 : $firstName.hashCode());
        final Object $lastName = this.getLastName();
        result = result * PRIME + ($lastName == null ? 43 : $lastName.hashCode());
        final Object $phoneNumber = this.getPhoneNumber();
        result = result * PRIME + ($phoneNumber == null ? 43 : $phoneNumber.hashCode());
        final Object $email = this.getEmail();
        result = result * PRIME + ($email == null ? 43 : $email.hashCode());
        final Object $gender = this.getGender();
        result = result * PRIME + ($gender == null ? 43 : $gender.hashCode());
        final Object $role = this.getRole();
        result = result * PRIME + ($role == null ? 43 : $role.hashCode());
        final Object $orderPoint = this.getOrderPoint();
        result = result * PRIME + ($orderPoint == null ? 43 : $orderPoint.hashCode());
        final Object $activated = this.getActivated();
        result = result * PRIME + ($activated == null ? 43 : $activated.hashCode());
        final Object $accountNonExpired = this.getAccountNonExpired();
        result = result * PRIME + ($accountNonExpired == null ? 43 : $accountNonExpired.hashCode());
        final Object $accountNonLocked = this.getAccountNonLocked();
        result = result * PRIME + ($accountNonLocked == null ? 43 : $accountNonLocked.hashCode());
        final Object $credentialsNonExpired = this.getCredentialsNonExpired();
        result = result * PRIME + ($credentialsNonExpired == null ? 43 : $credentialsNonExpired.hashCode());
        final Object $enabled = this.getEnabled();
        result = result * PRIME + ($enabled == null ? 43 : $enabled.hashCode());
        return result;
    }
}
