package com.bachlinh.order.security.auth.spi;

import com.bachlinh.order.entity.model.Customer;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Objects;

/**
 * Subclass of {@link AbstractAuthenticationToken} for holding {@code Customer}
 * to used into a security system.
 *
 * @author Hoang Minh Quan.
 */
public final class PrincipalHolder extends AbstractAuthenticationToken {

    private final Customer customer;
    private final String clientSecret;

    public PrincipalHolder(Customer customer, @Deprecated(forRemoval = true) String clientSecret) {
        super(customer.getAuthorities());
        this.customer = customer;
        this.clientSecret = clientSecret;
    }

    @Override
    public Object getCredentials() {
        return customer;
    }

    @Override
    public Object getPrincipal() {
        return getCredentials();
    }

    @Override
    public String getName() {
        return customer.getId();
    }

    @Deprecated(forRemoval = true)
    public String getClientSecret() {
        return clientSecret;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof PrincipalHolder other)) return false;
        if (!other.canEqual(this)) return false;
        final Object this$customer = this.customer;
        final Object other$customer = other.customer;
        if (!Objects.equals(this$customer, other$customer)) return false;
        final Object this$clientSecret = this.getClientSecret();
        final Object other$clientSecret = other.getClientSecret();
        return Objects.equals(this$clientSecret, other$clientSecret);
    }

    private boolean canEqual(final Object other) {
        return other instanceof PrincipalHolder;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $customer = this.customer;
        result = result * PRIME + ($customer == null ? 43 : $customer.hashCode());
        final Object $clientSecret = this.getClientSecret();
        result = result * PRIME + ($clientSecret == null ? 43 : $clientSecret.hashCode());
        return result;
    }
}
