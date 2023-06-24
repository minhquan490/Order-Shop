package com.bachlinh.order.web.service.impl;

import jakarta.persistence.criteria.JoinType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.ServiceComponent;
import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.enums.Country;
import com.bachlinh.order.entity.enums.Gender;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.entity.model.Address;
import com.bachlinh.order.entity.model.Cart;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Customer_;
import com.bachlinh.order.entity.model.EmailTemplate;
import com.bachlinh.order.entity.model.LoginHistory;
import com.bachlinh.order.entity.model.RefreshToken;
import com.bachlinh.order.exception.http.BadVariableException;
import com.bachlinh.order.exception.http.InvalidTokenException;
import com.bachlinh.order.exception.http.TemporaryTokenExpiredException;
import com.bachlinh.order.mail.model.MessageModel;
import com.bachlinh.order.mail.service.EmailSendingService;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.repository.EmailTemplateRepository;
import com.bachlinh.order.repository.LoginHistoryRepository;
import com.bachlinh.order.repository.query.Join;
import com.bachlinh.order.security.auth.spi.TemporaryTokenGenerator;
import com.bachlinh.order.security.auth.spi.TokenManager;
import com.bachlinh.order.security.handler.ClientSecretHandler;
import com.bachlinh.order.service.AbstractService;
import com.bachlinh.order.service.container.ContainerWrapper;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.parser.AddressParser;
import com.bachlinh.order.web.dto.form.CrudCustomerForm;
import com.bachlinh.order.web.dto.form.LoginForm;
import com.bachlinh.order.web.dto.form.RegisterForm;
import com.bachlinh.order.web.dto.form.admin.CustomerCreateForm;
import com.bachlinh.order.web.dto.resp.CustomerInformationResp;
import com.bachlinh.order.web.dto.resp.CustomerResp;
import com.bachlinh.order.web.dto.resp.LoginResp;
import com.bachlinh.order.web.dto.resp.RegisterResp;
import com.bachlinh.order.web.dto.resp.TableCustomerInfoResp;
import com.bachlinh.order.web.service.business.ForgotPasswordService;
import com.bachlinh.order.web.service.business.LoginService;
import com.bachlinh.order.web.service.business.LogoutService;
import com.bachlinh.order.web.service.business.RegisterService;
import com.bachlinh.order.web.service.common.CustomerService;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServiceComponent
@ActiveReflection
public class CustomerServiceImpl extends AbstractService<CustomerInformationResp, CrudCustomerForm> implements CustomerService, LoginService, RegisterService, LogoutService, ForgotPasswordService {
    private static final String BOT_EMAIL = "bachlinhshopadmin@story-community.iam.gserviceaccount.com";
    private final Map<String, TempTokenHolder> tempTokenMap = new ConcurrentHashMap<>();

    private PasswordEncoder passwordEncoder;
    private EntityFactory entityFactory;
    private CustomerRepository customerRepository;
    private TokenManager tokenManager;
    private LoginHistoryRepository loginHistoryRepository;
    private ClientSecretHandler clientSecretHandler;
    private EmailSendingService emailSendingService;
    private TemporaryTokenGenerator tokenGenerator;
    private EmailTemplateRepository emailTemplateRepository;

    @DependenciesInitialize
    @ActiveReflection
    public CustomerServiceImpl(ThreadPoolTaskExecutor executor, ContainerWrapper wrapper, @Value("${active.profile}") String profile) {
        super(executor, wrapper, profile);
        ThreadPoolTaskScheduler scheduler = getContainerResolver().getDependenciesResolver().resolveDependencies(ThreadPoolTaskScheduler.class);
        scheduler.schedule(() -> tempTokenMap.entrySet().removeIf(entry -> entry.getValue().getExpireTime().isAfter(LocalDateTime.now())), new CronTrigger("0 0 * * * *"));
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
        if (emailSendingService == null) {
            emailSendingService = resolver.resolveDependencies(EmailSendingService.class);
        }
        if (tokenGenerator == null) {
            tokenGenerator = resolver.resolveDependencies(TemporaryTokenGenerator.class);
        }
        if (emailTemplateRepository == null) {
            emailTemplateRepository = resolver.resolveDependencies(EmailTemplateRepository.class);
        }
    }

    @Override
    public CustomerInformationResp getCustomerInformation(String customerId) {
        inject();
        Customer customer = customerRepository.getCustomerById(customerId, false);
        return CustomerInformationResp.toDto(customer);
    }

    @Override
    public Page<CustomerResp> getFullInformationOfCustomer(Pageable pageable) {
        inject();
        return new PageImpl<>(
                customerRepository.getAll(pageable, Sort.by(Customer_.ID))
                        .stream()
                        .map(CustomerResp::toDto)
                        .toList());
    }

    @Override
    public Collection<TableCustomerInfoResp> getCustomerDataTable() {
        return customerRepository.getAll(PageRequest.of(1, 500), Sort.unsorted())
                .stream()
                .map(TableCustomerInfoResp::new)
                .toList();
    }

    @Override
    public CustomerInformationResp saveCustomer(CustomerCreateForm customerCreateForm) {
        inject();
        var customer = entityFactory.getEntity(Customer.class);
        customer.setFirstName(customerCreateForm.getFirstName());
        customer.setLastName(customerCreateForm.getLastName());
        customer.setPhoneNumber(customerCreateForm.getPhone());
        customer.setEmail(customerCreateForm.getEmail());
        customer.setGender(Gender.of(customerCreateForm.getGender()).name());
        customer.setRole(Role.of(customerCreateForm.getRole()).name());
        customer.setUsername(customerCreateForm.getUsername());
        customer.setPassword(passwordEncoder.encode(customerCreateForm.getPassword()));
        var addressForm = customerCreateForm.getAddress();
        var address = entityFactory.getEntity(Address.class);
        address.setCustomer(customer);
        address.setCountry(Country.VIET_NAM.getCountry());
        address.setCity(addressForm.getProvince());
        address.setValue(AddressParser.parseVietNamAddress(addressForm.getHouseAddress(), addressForm.getWard(), addressForm.getDistrict(), addressForm.getProvince()));
        customer.getAddresses().add(address);
        customer = customerRepository.saveCustomer(customer);
        return CustomerInformationResp.toDto(customer);
    }

    @Override
    public LoginResp login(LoginForm loginForm, NativeRequest<?> request) {
        inject();
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
        inject();
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
        inject();
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

    @Override
    public void sendEmailResetPassword(String email) {
        inject();
        String urlResetPassword = MessageFormat.format("https://{0}:{1}{2}", getEnvironment().getProperty("server.address"), getEnvironment().getProperty("server.port"), getEnvironment().getProperty("shop.url.customer.reset.password"));
        String tempToken = tokenGenerator.generateTempToken();
        TempTokenHolder holder = new TempTokenHolder(LocalDateTime.now(), email);
        tempTokenMap.put(tempToken, holder);
        EmailTemplate emailTemplate = emailTemplateRepository.getEmailTemplate("Reset password");
        if (emailTemplate != null) {
            MessageModel model = new MessageModel(BOT_EMAIL);
            model.setBody(MessageFormat.format(emailTemplate.getContent(), urlResetPassword));
            model.setCharset(StandardCharsets.UTF_8);
            model.setToAddress(email);
            model.setSubject("Reset your password");
            model.setContentType(MediaType.TEXT_PLAIN_VALUE);
            emailSendingService.send(model);
        }
    }

    @Override
    public void resetPassword(String temporaryToken, String newPassword) {
        inject();
        TempTokenHolder holder = tempTokenMap.get(temporaryToken);
        if (holder == null) {
            throw new InvalidTokenException("Your token is invalid");
        }
        LocalDateTime expireTime = tempTokenMap.get(temporaryToken).getExpireTime();
        if (expireTime.isAfter(LocalDateTime.now())) {
            throw new TemporaryTokenExpiredException("Temp token is expiry");
        }
        Customer customer = customerRepository.getCustomerByEmail(holder.getEmail());
        String hashedPassword = passwordEncoder.encode(newPassword);
        customer.setPassword(hashedPassword);
        customerRepository.updateCustomer(customer);
        tempTokenMap.remove(temporaryToken, holder);
    }

    private static class TempTokenHolder {
        private final LocalDateTime expireTime;
        private final String email;

        TempTokenHolder(LocalDateTime expireTime, String email) {
            this.email = email;
            this.expireTime = expireTime;
        }

        public LocalDateTime getExpireTime() {
            return expireTime;
        }

        public String getEmail() {
            return email;
        }
    }
}
