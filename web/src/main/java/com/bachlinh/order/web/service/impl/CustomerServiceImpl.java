package com.bachlinh.order.web.service.impl;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.ServiceComponent;
import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.Cart;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Customer_;
import com.bachlinh.order.entity.model.LoginHistory;
import com.bachlinh.order.entity.model.RefreshToken;
import com.bachlinh.order.exception.http.BadVariableException;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.repository.LoginHistoryRepository;
import com.bachlinh.order.repository.query.Join;
import com.bachlinh.order.security.auth.spi.TokenManager;
import com.bachlinh.order.security.handler.ClientSecretHandler;
import com.bachlinh.order.service.AbstractService;
import com.bachlinh.order.service.container.ContainerWrapper;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.form.CrudCustomerForm;
import com.bachlinh.order.web.dto.form.LoginForm;
import com.bachlinh.order.web.dto.form.RegisterForm;
import com.bachlinh.order.web.dto.resp.CustomerInformationResp;
import com.bachlinh.order.web.dto.resp.CustomerResp;
import com.bachlinh.order.web.dto.resp.LoginResp;
import com.bachlinh.order.web.dto.resp.RegisterResp;
import com.bachlinh.order.web.service.business.LoginService;
import com.bachlinh.order.web.service.business.LogoutService;
import com.bachlinh.order.web.service.business.RegisterService;
import com.bachlinh.order.web.service.common.CustomerService;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@ServiceComponent
@ActiveReflection
public class CustomerServiceImpl extends AbstractService<CustomerInformationResp, CrudCustomerForm> implements CustomerService, LoginService, RegisterService, LogoutService {
    private PasswordEncoder passwordEncoder;
    private EntityFactory entityFactory;
    private CustomerRepository customerRepository;
    private TokenManager tokenManager;
    private LoginHistoryRepository loginHistoryRepository;
    private ClientSecretHandler clientSecretHandler;

    @DependenciesInitialize
    @ActiveReflection
    public CustomerServiceImpl(ThreadPoolTaskExecutor executor, ContainerWrapper wrapper, String profile) {
        super(executor, wrapper, profile);
    }

    @Override
    protected CustomerInformationResp doSave(CrudCustomerForm param) {
        Customer customer = CrudCustomerForm.toCustomer(param, entityFactory, passwordEncoder);
        customer = customerRepository.saveCustomer(customer);
        return CustomerInformationResp.toDto(customer);
    }

    @Override
    protected CustomerInformationResp doUpdate(CrudCustomerForm param) {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String password = param.getPassword();
        String email = param.getEmail();
        String phone = param.getPhoneNumber();
        if (!password.isBlank()) {
            customer.setPassword(passwordEncoder.encode(password));
        }
        if (!email.isBlank()) {
            customer.setEmail(email);
            customer.setActivated(false);
        }
        if (!phone.isBlank()) {
            customer.setPhoneNumber(phone);
            customer.setActivated(false);
        }
        customer = customerRepository.updateCustomer(customer);
        return CustomerInformationResp.toDto(customer);
    }

    @Override
    protected CustomerInformationResp doDelete(CrudCustomerForm param) {
        var joinBuilder = Join.builder();
        joinBuilder.attribute(Customer_.ADDRESSES);
        joinBuilder.type(JoinType.INNER);
        Customer customer = customerRepository.getCustomerUseJoin(param.getId(), List.of(joinBuilder.build()));
        //TODO add serialize deleted customer to file
        return CustomerInformationResp.toDto(customer);
    }

    @Override
    protected CustomerInformationResp doGetOne(CrudCustomerForm param) {
        Customer customer = customerRepository.getCustomerById(param.getId(), true);
        return CustomerInformationResp.toDto(customer);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <K, X extends Iterable<K>> X doGetList(CrudCustomerForm param) {
        int page;
        int pageSize;
        try {
            page = Integer.parseInt(param.getPage());
            pageSize = Integer.parseInt(param.getPageSize());
        } catch (NumberFormatException e) {
            throw new BadVariableException("Page and page size must be int format");
        }
        List<CustomerResp> customerResps = customerRepository.getAll(PageRequest.of(page, pageSize), Sort.by(Customer_.ID))
                .stream()
                .map(CustomerResp::toDto)
                .toList();
        return (X) new PageImpl<>(customerResps);
    }


    @Override
    protected void inject() {
        DependenciesResolver resolver = getContainerResolver().getDependenciesResolver();
        if (passwordEncoder == null) {
            passwordEncoder = resolver.resolveDependencies(PasswordEncoder.class);
        }
        if (entityFactory == null) {
            entityFactory = resolver.resolveDependencies(EntityFactory.class);
        }
        if (customerRepository == null) {
            customerRepository = resolver.resolveDependencies(CustomerRepository.class);
        }
        if (tokenManager == null) {
            tokenManager = resolver.resolveDependencies(TokenManager.class);
        }
        if (loginHistoryRepository == null) {
            loginHistoryRepository = resolver.resolveDependencies(LoginHistoryRepository.class);
        }
        if (clientSecretHandler == null) {
            clientSecretHandler = resolver.resolveDependencies(ClientSecretHandler.class);
        }
    }

    @Override
    public CustomerInformationResp getCustomerInformation(String customerId) {
        Customer customer = customerRepository.getCustomerById(customerId, false);
        return CustomerInformationResp.toDto(customer);
    }

    @Override
    public Page<CustomerResp> getFullInformationOfCustomer(Pageable pageable) {
        return new PageImpl<>(
                customerRepository.getAll(pageable, Sort.by(Customer_.ID))
                        .stream()
                        .map(CustomerResp::toDto)
                        .toList());
    }

    @Override
    public LoginResp login(LoginForm loginForm, NativeRequest<?> request) {
        if (loginForm.username() == null || loginForm.username().isBlank() || loginForm.password() == null || loginForm.password().isBlank()) {
            return new LoginResp(null, null, false);
        }
        Customer customer = customerRepository.getCustomerByUsername(loginForm.username());
        if (customer == null) {
            return new LoginResp(null, null, false);
        }
        if (!passwordEncoder.matches(loginForm.password(), customer.getPassword())) {
            saveHistory(customer, request);
            return new LoginResp(null, null, false);
        }
        tokenManager.encode("customerId", customer.getId());
        tokenManager.encode("username", customer.getUsername());
        String accessToken = tokenManager.getTokenValue();
        RefreshToken refreshToken = tokenManager.getRefreshTokenGenerator().generateToken(customer.getId(), customer.getUsername());
        saveHistory(customer, request);
        return new LoginResp(refreshToken.getRefreshTokenValue(), accessToken, true);
    }

    @Override
    public RegisterResp register(RegisterForm registerForm) {
        Customer customer = registerForm.toCustomer(entityFactory, passwordEncoder);
        Cart cart = entityFactory.getEntity(Cart.class);
        cart.setCustomer(customer);
        customer.setCart(cart);
        try {
            customerRepository.saveCustomer(customer);
            return new RegisterResp("Register success", false);
        } catch (Exception e) {
            return new RegisterResp("Register failure", true);
        }
    }

    @Override
    public boolean logout(Customer customer, String secret) {
        customer = customerRepository.getCustomerById(customer.getId(), false);
        clientSecretHandler.removeClientSecret(customer.getRefreshToken().getRefreshTokenValue(), secret);
        customer.setRefreshToken(null);
        return customerRepository.updateCustomer(customer) != null;
    }

    private void saveHistory(Customer customer, NativeRequest<?> request) {
        getExecutor().execute(() -> {
            LoginHistory loginHistory = entityFactory.getEntity(LoginHistory.class);
            loginHistory.setCustomer(customer);
            loginHistory.setLastLoginTime(Timestamp.from(Instant.now()));
            loginHistory.setLoginIp(request.getCustomerIp());
            loginHistory.setSuccess(true);
            loginHistoryRepository.saveHistory(loginHistory);
        });
    }
}
