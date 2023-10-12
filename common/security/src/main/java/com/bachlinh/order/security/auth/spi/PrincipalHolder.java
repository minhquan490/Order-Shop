package com.bachlinh.order.security.auth.spi;

import com.bachlinh.order.entity.model.Customer;

import com.google.common.base.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PrincipalHolder that = (PrincipalHolder) o;
        return Objects.equal(customer, that.customer);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), customer);
    }
}
