package com.bachlinh.order.security.auth.spi;

import com.bachlinh.order.entity.model.Customer;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * Subclass of {@link AbstractAuthenticationToken} for holding {@code Customer}
 * to used into a security system.
 *
 * @author Hoang Minh Quan.
 */
public final class PrincipalHolder extends AbstractAuthenticationToken {

    private final Customer customer;

    public PrincipalHolder(Customer customer) {
        super(customer.getAuthorities());
        this.customer = customer;
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

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof PrincipalHolder)) return false;
        final PrincipalHolder other = (PrincipalHolder) o;
        if (!other.canEqual((Object) this)) return false;
        if (!super.equals(o)) return false;
        final Object this$customer = this.customer;
        final Object other$customer = other.customer;
        if (this$customer == null ? other$customer != null : !this$customer.equals(other$customer)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof PrincipalHolder;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $customer = this.customer;
        result = result * PRIME + ($customer == null ? 43 : $customer.hashCode());
        return result;
    }
}
