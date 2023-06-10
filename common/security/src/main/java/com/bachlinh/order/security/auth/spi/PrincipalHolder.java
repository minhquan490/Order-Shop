package com.bachlinh.order.security.auth.spi;

import lombok.EqualsAndHashCode;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import com.bachlinh.order.entity.model.Customer;

/**
 * Subclass of {@link AbstractAuthenticationToken} for holding {@code Customer}
 * to used into a security system.
 *
 * @author Hoang Minh Quan.
 */
@EqualsAndHashCode(callSuper = false)
public final class PrincipalHolder extends AbstractAuthenticationToken {

    private final Customer customer;
    private final String clientSecret;

    public PrincipalHolder(Customer customer, String clientSecret) {
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

    public String getClientSecret() {
        return clientSecret;
    }
}
