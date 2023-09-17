package com.bachlinh.order.web.service.impl;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.ServiceComponent;
import com.bachlinh.order.core.concurrent.RunnableType;
import com.bachlinh.order.core.concurrent.ThreadPoolManager;
import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.dto.DtoMapper;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.context.FieldUpdated;
import com.bachlinh.order.entity.enums.Gender;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.entity.model.Address;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.CustomerAccessHistory;
import com.bachlinh.order.entity.model.CustomerInfoChangeHistory;
import com.bachlinh.order.entity.model.Customer_;
import com.bachlinh.order.entity.model.District;
import com.bachlinh.order.entity.model.EmailTemplate;
import com.bachlinh.order.entity.model.LoginHistory;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.entity.model.Province;
import com.bachlinh.order.entity.model.RefreshToken;
import com.bachlinh.order.entity.model.TemporaryToken;
import com.bachlinh.order.entity.model.Ward;
import com.bachlinh.order.entity.repository.query.OrderBy;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.exception.http.BadVariableException;
import com.bachlinh.order.exception.http.InvalidTokenException;
import com.bachlinh.order.exception.http.ResourceNotFoundException;
import com.bachlinh.order.exception.http.TemporaryTokenExpiredException;
import com.bachlinh.order.exception.http.UnAuthorizationException;
import com.bachlinh.order.exception.system.common.CriticalException;
import com.bachlinh.order.mail.model.GmailMessage;
import com.bachlinh.order.mail.service.GmailSendingService;
import com.bachlinh.order.repository.AddressRepository;
import com.bachlinh.order.repository.CustomerAccessHistoryRepository;
import com.bachlinh.order.repository.CustomerInfoChangeHistoryRepository;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.repository.EmailTemplateRepository;
import com.bachlinh.order.repository.LoginHistoryRepository;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.repository.ProvinceRepository;
import com.bachlinh.order.repository.RefreshTokenRepository;
import com.bachlinh.order.repository.TemporaryTokenRepository;
import com.bachlinh.order.security.auth.spi.RefreshTokenHolder;
import com.bachlinh.order.security.auth.spi.TemporaryTokenGenerator;
import com.bachlinh.order.security.auth.spi.TokenManager;
import com.bachlinh.order.utils.DateTimeUtils;
import com.bachlinh.order.utils.JacksonUtils;
import com.bachlinh.order.utils.ResourceUtils;
import com.bachlinh.order.utils.ValidateUtils;
import com.bachlinh.order.web.dto.form.admin.customer.CustomerCreateForm;
import com.bachlinh.order.web.dto.form.admin.customer.CustomerDeleteForm;
import com.bachlinh.order.web.dto.form.admin.customer.CustomerUpdateInfoForm;
import com.bachlinh.order.web.dto.form.common.LoginForm;
import com.bachlinh.order.web.dto.form.customer.CustomerUpdateForm;
import com.bachlinh.order.web.dto.form.customer.RegisterForm;
import com.bachlinh.order.web.dto.resp.AnalyzeCustomerNewInMonthResp;
import com.bachlinh.order.web.dto.resp.ConfirmEmailResp;
import com.bachlinh.order.web.dto.resp.CustomerAccessHistoriesResp;
import com.bachlinh.order.web.dto.resp.CustomerBasicInformationResp;
import com.bachlinh.order.web.dto.resp.CustomerInfoResp;
import com.bachlinh.order.web.dto.resp.CustomerResp;
import com.bachlinh.order.web.dto.resp.CustomerUpdateDataHistoriesResp;
import com.bachlinh.order.web.dto.resp.LoginResp;
import com.bachlinh.order.web.dto.resp.MyInfoResp;
import com.bachlinh.order.web.dto.resp.RegisterResp;
import com.bachlinh.order.web.dto.resp.RevokeTokenResp;
import com.bachlinh.order.web.service.business.ConfirmEmailService;
import com.bachlinh.order.web.service.business.CustomerAnalyzeService;
import com.bachlinh.order.web.service.business.CustomerSearchingService;
import com.bachlinh.order.web.service.business.ForgotPasswordService;
import com.bachlinh.order.web.service.business.LoginService;
import com.bachlinh.order.web.service.business.LogoutService;
import com.bachlinh.order.web.service.business.RegisterService;
import com.bachlinh.order.web.service.business.RevokeAccessTokenService;
import com.bachlinh.order.web.service.common.CustomerAccessHistoriesService;
import com.bachlinh.order.web.service.common.CustomerInfoChangeService;
import com.bachlinh.order.web.service.common.CustomerService;
import com.bachlinh.order.web.service.common.EmailFolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@ServiceComponent
@ActiveReflection
@RequiredArgsConstructor(onConstructor = @__({@ActiveReflection, @DependenciesInitialize}))
// @formatter:off
public class CustomerServiceImpl implements CustomerService,
                                            LoginService,
                                            RegisterService,
                                            LogoutService,
                                            ForgotPasswordService,
                                            CustomerAnalyzeService,
                                            RevokeAccessTokenService,
                                            CustomerSearchingService,
                                            ConfirmEmailService,
                                            CustomerAccessHistoriesService,
                                            CustomerInfoChangeService { // @formatter:on

    private final PasswordEncoder passwordEncoder;
    private final EntityFactory entityFactory;
    private final CustomerRepository customerRepository;
    private final TokenManager tokenManager;
    private final LoginHistoryRepository loginHistoryRepository;
    private final GmailSendingService gmailSendingService;
    private final TemporaryTokenGenerator tokenGenerator;
    private final EmailTemplateRepository emailTemplateRepository;
    private final EmailFolderService emailFolderService;
    private final CustomerInfoChangeHistoryRepository customerInfoChangeHistoryRepository;
    private final TemporaryTokenRepository temporaryTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ProvinceRepository provinceRepository;
    private final AddressRepository addressRepository;
    private final CustomerAccessHistoryRepository customerAccessHistoryRepository;
    private final MessageSettingRepository messageSettingRepository;
    private final DtoMapper dtoMapper;
    private final ThreadPoolManager threadPoolManager;
    private String urlResetPassword;
    private String botEmail;

    @Override
    public MyInfoResp getMyInfo(String customerId) {
        Customer customer = customerRepository.getCustomerUpdatableInfo(customerId);
        return dtoMapper.map(customer, MyInfoResp.class);
    }

    @Override
    public Page<CustomerResp> getFullInformationOfCustomer(Pageable pageable) {
        OrderBy customerIdOrderBy = OrderBy.builder().column(Customer_.ID).type(OrderBy.Type.ASC).build();
        return new PageImpl<>(
                customerRepository.getAll(pageable, Collections.singleton(customerIdOrderBy))
                        .stream()
                        .map(customer -> dtoMapper.map(customer, CustomerResp.class))
                        .toList());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public CustomerResp saveCustomer(CustomerCreateForm customerCreateForm) {
        var customer = dtoMapper.map(customerCreateForm, Customer.class);
        customer = customerRepository.saveCustomer(customer);
        emailFolderService.createDefaultFolders(customer);
        return dtoMapper.map(customer, CustomerResp.class);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public CustomerResp updateCustomer(CustomerUpdateForm customerUpdateForm) {
        var customer = customerRepository.getCustomerInfoForUpdate(customerUpdateForm.getId());
        var oldCustomer = customer.clone();
        customer.setFirstName(customerUpdateForm.getFirstName());
        customer.setLastName(customerUpdateForm.getLastName());
        customer.setPhoneNumber(customerUpdateForm.getPhone());
        customer.setEmail(customerUpdateForm.getEmail());
        customer.setGender(Objects.requireNonNull(Gender.of(customerUpdateForm.getGender())).name());
        customer.setUsername(customerUpdateForm.getUsername());
        customer.setUpdatedFields(findUpdatedFields((Customer) oldCustomer, customer));
        customer = customerRepository.updateCustomer(customer);
        return dtoMapper.map(customer, CustomerResp.class);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public CustomerInfoResp updateCustomerFromAdminScreen(CustomerUpdateInfoForm customerUpdateInfoForm) {
        Customer customer = customerRepository.getCustomerInfoForUpdate(customerUpdateInfoForm.getId());
        customer.setFirstName(customerUpdateInfoForm.getFirstName());
        customer.setLastName(customerUpdateInfoForm.getLastName());
        customer.setPhoneNumber(customerUpdateInfoForm.getPhoneNumber());
        customer.setEmail(customerUpdateInfoForm.getEmail());
        customer.setGender(customerUpdateInfoForm.getGender());
        customer.setRole(Objects.requireNonNull(Role.of(customerUpdateInfoForm.getRole())).name());
        customer.setOrderPoint(customerUpdateInfoForm.getOrderPoint());
        customer.setActivated(customerUpdateInfoForm.getActivated());
        customer.setAccountNonExpired(customerUpdateInfoForm.getAccountNonExpired());
        customer.setAccountNonLocked(customerUpdateInfoForm.getAccountNonLocked());
        customer.setCredentialsNonExpired(customerUpdateInfoForm.getCredentialsNonExpired());
        customer.setEnabled(customerUpdateInfoForm.getEnabled());
        customerRepository.updateCustomer(customer);
        Set<Address> addressSet = new LinkedHashSet<>();
        if (customerUpdateInfoForm.getAddresses() != null) {
            for (var a : customerUpdateInfoForm.getAddresses()) {
                Province province = provinceRepository.getAddress(a.getProvinceId(), a.getDistrictId(), a.getWardId());
                if (province != null) {
                    Address address = entityFactory.getEntity(Address.class);
                    address.setCity(province.getName());
                    District district = province.getDistricts().get(0);
                    Ward ward = district.getWards().get(0);
                    address.setCountry("Viet Nam");
                    address.setValue(String.format("%s, %s, %s", a.getHouseNumber(), ward.getName(), district.getName()));
                    address.setCustomer(customer);
                    addressSet.add(address);
                }
            }
        }
        addressRepository.bulkSave(addressSet);
        return getCustomerInfo(customer.getId());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public CustomerResp deleteCustomer(CustomerDeleteForm customerDeleteForm) {
        var customer = customerRepository.getCustomerForDelete(customerDeleteForm.customerId());
        customerRepository.deleteCustomer(customer);
        return dtoMapper.map(customer, CustomerResp.class);
    }

    @Override
    public CustomerInfoResp getCustomerInfo(String customerId) {
        var customer = customerRepository.getFullInformation(customerId);
        var histories = loginHistoryRepository.getHistories(customer, 3);
        var changeHistories = customerInfoChangeHistoryRepository.getHistoriesChangeOfCustomer(customer, 3);
        var result = dtoMapper.map(customer, CustomerInfoResp.class);
        result.setLoginHistories(histories.stream()
                .map(loginHistory -> {
                    var historyResp = new CustomerInfoResp.LoginHistory();
                    historyResp.setId(Objects.requireNonNull(loginHistory.getId()).toString());
                    historyResp.setLastLoginTime(DateTimeUtils.convertOutputDateTime(loginHistory.getLastLoginTime()));
                    historyResp.setLoginIp(loginHistory.getLoginIp());
                    historyResp.setSuccess(loginHistory.getSuccess());
                    return historyResp;
                })
                .toList()
                .toArray(new CustomerInfoResp.LoginHistory[0]));
        result.setInfoChangeHistories(changeHistories.stream()
                .map(customerInfoChangeHistory -> {
                    var infoChangeHistory = new CustomerInfoResp.InfoChangeHistory();
                    infoChangeHistory.setId(customerInfoChangeHistory.getId());
                    infoChangeHistory.setFieldName(customerInfoChangeHistory.getFieldName());
                    infoChangeHistory.setTimeUpdate(DateTimeUtils.convertOutputDateTime(customerInfoChangeHistory.getTimeUpdate()));
                    infoChangeHistory.setOldValue(customerInfoChangeHistory.getOldValue());
                    return infoChangeHistory;
                })
                .toList()
                .toArray(new CustomerInfoResp.InfoChangeHistory[0]));
        return result;
    }

    @Override
    public CustomerBasicInformationResp basicCustomerInfo(String accessToken, NativeResponse<?> nativeResponse) {
        Map<String, Object> tokenClaims = tokenManager.getClaimsFromToken(accessToken);
        if (tokenClaims.isEmpty()) {
            throw new UnAuthorizationException("Invalid token", "");
        }
        String customerId = (String) tokenClaims.get(Customer_.ID);
        Customer customer = customerRepository.getCustomerBasicInformation(customerId);
        if (customer == null) {
            throw new UnAuthorizationException("Invalid token", "");
        }
        if (customer.getCustomerMedia() != null) {
            nativeResponse.setActivePushBuilder(true);
        }
        return dtoMapper.map(customer, CustomerBasicInformationResp.class);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public LoginResp login(LoginForm loginForm, NativeRequest<?> request) {
        Customer customer = customerRepository.getCustomerForLogin(loginForm.username());
        if (customer == null) {
            throw new UnAuthorizationException("Wrong username or password", "");
        }
        if (!passwordEncoder.matches(loginForm.password(), customer.getPassword())) {
            saveHistory(customer, request, false);
            throw new UnAuthorizationException("Wrong username or password", "");
        }
        tokenManager.encode(Customer_.ID, customer.getId());
        tokenManager.encode(Customer_.USERNAME, customer.getUsername());
        String accessToken = tokenManager.getTokenValue();
        RefreshToken refreshToken = refreshTokenRepository.getRefreshTokenByCustomer(customer);
        if (refreshToken == null) {
            refreshToken = tokenManager.getRefreshTokenGenerator().generateToken(customer.getId(), customer.getUsername());
        } else {
            String tokenValue = tokenManager.getRefreshTokenGenerator().generateToken(customer.getId(), customer.getUsername()).getRefreshTokenValue();
            Instant timeCreated = Instant.now();
            refreshToken.setTimeCreated(Timestamp.from(timeCreated));
            refreshToken.setTimeExpired(DateTimeUtils.calculateTimeRefreshTokenExpired(timeCreated));
            refreshToken.setRefreshTokenValue(tokenValue);
        }
        refreshTokenRepository.saveRefreshToken(refreshToken);
        saveHistory(customer, request, true);
        return new LoginResp(refreshToken.getRefreshTokenValue(), accessToken, true, "");
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public RegisterResp register(RegisterForm registerForm) {
        Customer customer = dtoMapper.map(registerForm, Customer.class);
        try {
            customerRepository.saveCustomer(customer);
            return new RegisterResp("Register success, we will sent you confirm email for active your account", false, null);
        } catch (Exception e) {
            return new RegisterResp("Register failure", true, e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean logout(Customer customer) {
        RefreshToken refreshToken = refreshTokenRepository.getRefreshTokenByCustomer(customer);
        refreshToken.setRefreshTokenValue(null);
        refreshToken.setTimeExpired(null);
        refreshToken.setTimeExpired(null);
        return refreshTokenRepository.saveRefreshToken(refreshToken) != null;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void saveHistory(Customer customer, NativeRequest<?> request, boolean isSuccess) {
        threadPoolManager.execute(() -> {
            LoginHistory loginHistory = entityFactory.getEntity(LoginHistory.class);
            loginHistory.setCustomer(customer);
            loginHistory.setLastLoginTime(Timestamp.from(Instant.now()));
            loginHistory.setLoginIp(request.getCustomerIp());
            loginHistory.setSuccess(isSuccess);
            loginHistoryRepository.saveHistory(loginHistory);
        }, RunnableType.SERVICE);
    }

    @Override
    public void sendEmailResetPassword(String email) {
        Customer customer = customerRepository.getCustomerForResetPassword(email);
        if (customer == null) {
            throw new BadVariableException(String.format("Email %s is not existed", email));
        }
        String tempToken = tokenGenerator.generateTempToken();

        TemporaryToken token = entityFactory.getEntity(TemporaryToken.class);
        if (token == null) {
            token = entityFactory.getEntity(TemporaryToken.class);
        }
        token.setValue(tempToken);
        token.setAssignCustomer(customer);
        token.setExpiryTime(Timestamp.from(Instant.now(Clock.systemDefaultZone()).plus(10, ChronoUnit.MINUTES)));

        temporaryTokenRepository.saveTemporaryToken(token);

        EmailTemplate emailTemplate = emailTemplateRepository.getDefaultEmailTemplate("Reset password");
        if (emailTemplate != null) {
            GmailMessage model = new GmailMessage(botEmail);
            model.setBody(MessageFormat.format(emailTemplate.getContent(), urlResetPassword));
            model.setCharset(StandardCharsets.UTF_8);
            model.setToAddress(email);
            model.setSubject("Reset your password");
            model.setContentType(MediaType.TEXT_PLAIN_VALUE);
            gmailSendingService.send(model);
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void resetPassword(String temporaryToken, String newPassword) {
        TemporaryToken token = temporaryTokenRepository.getTemporaryToken(temporaryToken);
        if (token == null) {
            throw new InvalidTokenException("Your token is invalid");
        }
        LocalDateTime expireTime = token.getExpiryTime().toLocalDateTime();
        if (expireTime.isAfter(LocalDateTime.now())) {
            throw new TemporaryTokenExpiredException("Temp token is expiry");
        }
        Customer customer = token.getAssignCustomer();
        String hashedPassword = passwordEncoder.encode(newPassword);
        customer.setPassword(hashedPassword);
        customerRepository.updateCustomer(customer);
    }

    private List<FieldUpdated> findUpdatedFields(Customer oldCustomer, Customer newCustomer) {
        var fields = new ArrayList<FieldUpdated>();
        if (!oldCustomer.getPhoneNumber().equals(newCustomer.getPhoneNumber())) {
            fields.add(new FieldUpdated("PHONE_NUMBER", oldCustomer.getPhoneNumber(), newCustomer::getPhoneNumber));
        }
        if (!oldCustomer.getEmail().equals(newCustomer.getEmail())) {
            fields.add(new FieldUpdated("EMAIL", oldCustomer.getEmail(), newCustomer::getEmail));
        }
        if (!oldCustomer.getRole().equals(newCustomer.getRole())) {
            fields.add(new FieldUpdated("ROLE", oldCustomer.getRole(), newCustomer::getRole));
        }
        if (!oldCustomer.getOrderPoint().equals(newCustomer.getOrderPoint())) {
            fields.add(new FieldUpdated("ORDER_POINT", oldCustomer.getOrderPoint(), newCustomer::getOrderPoint));
        }
        if (oldCustomer.isActivated() != newCustomer.isActivated()) {
            fields.add(new FieldUpdated("ENABLED", oldCustomer.isActivated(), newCustomer::isActivated));
        }
        return fields;
    }

    @Override
    public AnalyzeCustomerNewInMonthResp analyzeCustomerNewInMonth() {
        var template = "select t.* from (select count(c.id) as first, ({0}) as second, ({1}) as third, ({2}) as fourth, ({3}) as last from Customer c where c.created_date between :firstStart and :firstEnd) as t";
        var secondStatement = "select count(c.id) from Customer c where c.created_date between :secondStart and :secondEnd";
        var thirdStatement = "select count(c.id) from Customer c where c.created_date between :thirdStart and :thirdEnd";
        var fourthStatement = "select count(c.id) from Customer c where c.created_date between :fourthStart and :fourthEnd";
        var lastStatement = "select count(c.id) from Customer c where c.created_date between :lastStart and :lastEnd";
        var query = MessageFormat.format(template, secondStatement, thirdStatement, fourthStatement, lastStatement);
        var attributes = new HashMap<String, Object>(10);
        var now = LocalDateTime.now();
        var firstParam = Timestamp.valueOf(now.plusWeeks(-5));
        var secondParam = Timestamp.valueOf(now.plusWeeks(-4));
        var thirdParam = Timestamp.valueOf(now.plusWeeks(-3));
        var fourthParam = Timestamp.valueOf(now.plusWeeks(-2));
        var fifthParam = Timestamp.valueOf(now.plusWeeks(-1));
        attributes.put("firstStart", firstParam);
        attributes.put("firstEnd", secondParam);
        attributes.put("secondStart", secondParam);
        attributes.put("secondEnd", thirdParam);
        attributes.put("thirdStart", thirdParam);
        attributes.put("thirdEnd", fourthParam);
        attributes.put("fourthStart", fourthParam);
        attributes.put("fourthEnd", fifthParam);
        attributes.put("lastStart", fifthParam);
        attributes.put("lastEnd", Timestamp.valueOf(now));
        var result = customerRepository.getResultList(query, attributes, AnalyzeCustomerNewInMonthResp.ResultSet.class).get(0);
        return dtoMapper.map(result, AnalyzeCustomerNewInMonthResp.class);
    }

    @Override
    public RevokeTokenResp revokeToken(String refreshToken) {
        RefreshTokenHolder holder = tokenManager.validateRefreshToken(refreshToken);
        String jwt;
        if (holder.isNonNull()) {
            Customer customer = holder.getValue().getCustomer();
            tokenManager.encode(Customer_.ID, customer.getId());
            tokenManager.encode(Customer_.USERNAME, customer.getUsername());
            jwt = tokenManager.getTokenValue();
        } else {
            throw new UnAuthorizationException("Token is expired", "");
        }
        return new RevokeTokenResp(jwt, refreshToken);
    }

    @Override
    public Collection<CustomerResp> search(String query) {
        var context = entityFactory.getEntityContext(Customer.class);
        var ids = context.search(Customer.class, query);
        var customers = customerRepository.getCustomerByIds(ids);
        return dtoMapper.map(customers, CustomerResp.class);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public ConfirmEmailResp confirmEmail(String temporaryToken) {
        TemporaryToken token = temporaryTokenRepository.getTemporaryToken(temporaryToken);
        if (token == null) {
            throw new ResourceNotFoundException("Not found", "");
        }
        Customer customer = token.getAssignCustomer();
        customer.setActivated(true);
        customerRepository.updateCustomer(customer);
        return new ConfirmEmailResp(HttpStatus.OK.value(), new String[]{"Confirm email successfully"});
    }

    @DependenciesInitialize
    public void setUrlResetPassword(@Value("${active.profile}") String profile) {
        Environment environment = Environment.getInstance(profile);
        this.urlResetPassword = MessageFormat.format("https://{0}:{1}{2}", environment.getProperty("server.address"), environment.getProperty("server.port"), environment.getProperty("shop.url.customer.reset.password"));
        try {
            var node = JacksonUtils.readJsonFile(ResourceUtils.getURL(environment.getProperty("google.email.credentials")));
            this.botEmail = node.get("client_email").asText();
            if (this.botEmail.isEmpty()) {
                throw new CriticalException("Google credentials json not valid");
            }
        } catch (FileNotFoundException e) {
            throw new CriticalException("Google credentials not found", e);
        }
    }

    @Override
    public CustomerAccessHistoriesResp getAccessHistories(NativeRequest<?> request) {
        long page = getPage(request);
        long pageSize = getPageSize(request);
        String customerId = getCustomerId(request, request.getUrl());
        Customer customer = entityFactory.getEntity(Customer.class);
        customer.setId(customerId);
        Collection<CustomerAccessHistory> customerAccessHistories = customerAccessHistoryRepository.getHistoriesOfCustomer(customer, page, pageSize);
        Collection<CustomerAccessHistoriesResp.CustomerAccessHistoriesInfo> results = dtoMapper.map(customerAccessHistories, CustomerAccessHistoriesResp.CustomerAccessHistoriesInfo.class);
        CustomerAccessHistoriesResp resp = new CustomerAccessHistoriesResp();
        resp.setAccessHistories(results);
        Long totalCustomerAccessHistories = customerAccessHistoryRepository.countAccessHistoriesOfCustomer(customerId);
        resp.setTotalHistories(totalCustomerAccessHistories);
        resp.setPage(page);
        resp.setPageSize(pageSize);
        return resp;
    }

    @Override
    public CustomerUpdateDataHistoriesResp getCustomerInfoChangeHistories(NativeRequest<?> request) {
        long page = getPage(request);
        long pageSize = getPageSize(request);
        String customerId = getCustomerId(request, request.getUrl());
        Collection<CustomerInfoChangeHistory> histories = customerInfoChangeHistoryRepository.getHistoriesChangeOfCustomer(customerId, page, pageSize);
        Collection<CustomerUpdateDataHistoriesResp.CustomerUpdateDataHistoryInfo> historyInfos = dtoMapper.map(histories, CustomerUpdateDataHistoriesResp.CustomerUpdateDataHistoryInfo.class);

        CustomerUpdateDataHistoriesResp resp = new CustomerUpdateDataHistoriesResp();
        resp.setHistoryInfos(historyInfos);

        Long totalHistories = customerInfoChangeHistoryRepository.countChangeHistories(customerId);
        resp.setTotalHistories(totalHistories);
        resp.setPage(page);
        resp.setPageSize(pageSize);

        return resp;
    }

    private String getCustomerId(NativeRequest<?> nativeRequest, String path) {
        String customerId = nativeRequest.getUrlQueryParam().getFirst("customerId");
        if (!StringUtils.hasText(customerId)) {
            MessageSetting messageSetting = messageSettingRepository.getMessageById("MSG-000008");
            String errorContent = MessageFormat.format(messageSetting.getValue(), "Customer");
            throw new ResourceNotFoundException(errorContent, path);
        }
        return customerId;
    }

    private long getPageSize(NativeRequest<?> nativeRequest) {
        String pageSizeRequestParam = nativeRequest.getUrlQueryParam().getFirst("pageSize");
        if (ValidateUtils.isNumber(pageSizeRequestParam)) {
            return Long.parseLong(pageSizeRequestParam);
        } else {
            return 50L;
        }
    }

    private long getPage(NativeRequest<?> nativeRequest) {
        String pageRequestParam = nativeRequest.getUrlQueryParam().getFirst("page");
        if (ValidateUtils.isNumber(pageRequestParam)) {
            return Long.parseLong(pageRequestParam);
        } else {
            return 1L;
        }
    }
}
