package com.bachlinh.order.web.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.ServiceComponent;
import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.context.spi.FieldUpdated;
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
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.exception.http.InvalidTokenException;
import com.bachlinh.order.exception.http.TemporaryTokenExpiredException;
import com.bachlinh.order.mail.model.MessageModel;
import com.bachlinh.order.mail.service.EmailSendingService;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.repository.EmailTemplateRepository;
import com.bachlinh.order.repository.LoginHistoryRepository;
import com.bachlinh.order.security.auth.spi.TemporaryTokenGenerator;
import com.bachlinh.order.security.auth.spi.TokenManager;
import com.bachlinh.order.security.handler.ClientSecretHandler;
import com.bachlinh.order.utils.parser.AddressParser;
import com.bachlinh.order.web.dto.form.LoginForm;
import com.bachlinh.order.web.dto.form.RegisterForm;
import com.bachlinh.order.web.dto.form.admin.CustomerCreateForm;
import com.bachlinh.order.web.dto.form.admin.CustomerDeleteForm;
import com.bachlinh.order.web.dto.form.admin.CustomerUpdateForm;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

@ServiceComponent
@ActiveReflection
public class CustomerServiceImpl implements CustomerService, LoginService, RegisterService, LogoutService, ForgotPasswordService {
    private static final String BOT_EMAIL = "bachlinhshopadmin@story-community.iam.gserviceaccount.com";
    private final Map<String, TempTokenHolder> tempTokenMap = new ConcurrentHashMap<>();

    private final PasswordEncoder passwordEncoder;
    private final EntityFactory entityFactory;
    private final CustomerRepository customerRepository;
    private final TokenManager tokenManager;
    private final LoginHistoryRepository loginHistoryRepository;
    private final ClientSecretHandler clientSecretHandler;
    private final EmailSendingService emailSendingService;
    private final TemporaryTokenGenerator tokenGenerator;
    private final EmailTemplateRepository emailTemplateRepository;
    private final Executor executor;
    private final String urlResetPassword;

    @DependenciesInitialize
    @ActiveReflection
    public CustomerServiceImpl(PasswordEncoder passwordEncoder,
                               EntityFactory entityFactory,
                               CustomerRepository customerRepository,
                               TokenManager tokenManager,
                               LoginHistoryRepository loginHistoryRepository,
                               ClientSecretHandler clientSecretHandler,
                               EmailSendingService emailSendingService,
                               TemporaryTokenGenerator tokenGenerator,
                               EmailTemplateRepository emailTemplateRepository,
                               Executor executor,
                               @Value("${active.profile}") String profile) {
        this.passwordEncoder = passwordEncoder;
        this.entityFactory = entityFactory;
        this.customerRepository = customerRepository;
        this.tokenManager = tokenManager;
        this.loginHistoryRepository = loginHistoryRepository;
        this.clientSecretHandler = clientSecretHandler;
        this.emailSendingService = emailSendingService;
        this.tokenGenerator = tokenGenerator;
        this.emailTemplateRepository = emailTemplateRepository;
        this.executor = executor;
        Environment environment = Environment.getInstance(profile);
        this.urlResetPassword = MessageFormat.format("https://{0}:{1}{2}", environment.getProperty("server.address"), environment.getProperty("server.port"), environment.getProperty("shop.url.customer.reset.password"));
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
    public Collection<TableCustomerInfoResp> getCustomerDataTable() {
        return customerRepository.getAll(PageRequest.of(1, 500), Sort.unsorted())
                .stream()
                .map(TableCustomerInfoResp::new)
                .toList();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public CustomerInformationResp saveCustomer(CustomerCreateForm customerCreateForm) {
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
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public CustomerInformationResp updateCustomer(CustomerUpdateForm customerUpdateForm) {
        var customer = customerRepository.getCustomerById(customerUpdateForm.getId(), false);
        var oldCustomer = customer.clone();
        customer.setFirstName(customerUpdateForm.getFirstName());
        customer.setLastName(customerUpdateForm.getLastName());
        customer.setPhoneNumber(customerUpdateForm.getPhone());
        customer.setEmail(customerUpdateForm.getEmail());
        customer.setGender(Gender.of(customerUpdateForm.getGender()).name());
        customer.setUsername(customerUpdateForm.getUsername());
        customer.setUpdatedFields(findUpdatedFields((Customer) oldCustomer, customer));
        customer = customerRepository.updateCustomer(customer);
        return CustomerInformationResp.toDto(customer);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public CustomerInformationResp deleteCustomer(CustomerDeleteForm customerDeleteForm) {
        var customer = customerRepository.getCustomerById(customerDeleteForm.customerId(), true);
        customerRepository.deleteCustomer(customer);
        return CustomerInformationResp.toDto(customer);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public LoginResp login(LoginForm loginForm, NativeRequest<?> request) {
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
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
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

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void saveHistory(Customer customer, NativeRequest<?> request) {
        executor.execute(() -> {
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
        TempTokenHolder holder = tempTokenMap.get(temporaryToken);
        if (holder == null) {
            throw new InvalidTokenException("Your token is invalid");
        }
        LocalDateTime expireTime = tempTokenMap.get(temporaryToken).expireTime();
        if (expireTime.isAfter(LocalDateTime.now())) {
            throw new TemporaryTokenExpiredException("Temp token is expiry");
        }
        Customer customer = customerRepository.getCustomerByEmail(holder.email());
        String hashedPassword = passwordEncoder.encode(newPassword);
        customer.setPassword(hashedPassword);
        customerRepository.updateCustomer(customer);
        tempTokenMap.remove(temporaryToken, holder);
    }

    private Collection<FieldUpdated> findUpdatedFields(Customer oldCustomer, Customer newCustomer) {
        var fields = new ArrayList<FieldUpdated>();
        if (!oldCustomer.getPhoneNumber().equals(newCustomer.getPhoneNumber())) {
            fields.add(new FieldUpdated("PHONE_NUMBER", oldCustomer.getPhoneNumber()));
        }
        if (!oldCustomer.getEmail().equals(newCustomer.getEmail())) {
            fields.add(new FieldUpdated("EMAIL", oldCustomer.getEmail()));
        }
        if (!oldCustomer.getRole().equals(newCustomer.getRole())) {
            fields.add(new FieldUpdated("ROLE", oldCustomer.getRole()));
        }
        if (!oldCustomer.getOrderPoint().equals(newCustomer.getOrderPoint())) {
            fields.add(new FieldUpdated("ORDER_POINT", oldCustomer.getOrderPoint().toString()));
        }
        if (oldCustomer.isActivated() != newCustomer.isActivated()) {
            fields.add(new FieldUpdated("ENABLED", String.valueOf(oldCustomer.isActivated())));
        }
        return fields;
    }

    private record TempTokenHolder(LocalDateTime expireTime, String email) {
    }
}
