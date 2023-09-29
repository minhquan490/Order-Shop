package com.bachlinh.order.trigger;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.ApplyOn;
import com.bachlinh.order.annotation.Ignore;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.enums.TriggerExecution;
import com.bachlinh.order.entity.enums.TriggerMode;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Email;
import com.bachlinh.order.entity.model.EmailFolders;
import com.bachlinh.order.entity.model.EmailTemplate;
import com.bachlinh.order.entity.model.TemporaryToken;
import com.bachlinh.order.entity.trigger.AbstractTrigger;
import com.bachlinh.order.mail.template.BindingModel;
import com.bachlinh.order.mail.template.EmailTemplateProcessor;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.repository.EmailFoldersRepository;
import com.bachlinh.order.repository.EmailRepository;
import com.bachlinh.order.repository.EmailTemplateRepository;
import com.bachlinh.order.repository.TemporaryTokenRepository;
import com.bachlinh.order.security.auth.spi.TemporaryTokenGenerator;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@ActiveReflection
@ApplyOn(entity = Customer.class)
@Ignore
public class CustomerCreateSendingConfirmEmailTrigger extends AbstractTrigger<Customer> {
    private EmailTemplateRepository emailTemplateRepository;
    private EmailFoldersRepository emailFoldersRepository;
    private TemporaryTokenRepository temporaryTokenRepository;
    private CustomerRepository customerRepository;
    private EmailTemplateProcessor emailTemplateProcessor;
    private TemporaryTokenGenerator tokenGenerator;
    private EmailRepository emailRepository;
    private EntityFactory entityFactory;

    @Override
    public TriggerMode getMode() {
        return TriggerMode.AFTER;
    }

    @Override
    public TriggerExecution[] getExecuteOn() {
        return new TriggerExecution[]{TriggerExecution.ON_INSERT};
    }

    @Override
    public String getTriggerName() {
        return "customerCreateSendingConfirmEmail";
    }

    @Override
    protected void doExecute(Customer entity) {
        EmailFolders defaultEmailFolder = emailFoldersRepository.getEmailFolderByName("Default", entity);
        EmailTemplate emailTemplate = emailTemplateRepository.getDefaultEmailTemplate("Confirm email");

        if (defaultEmailFolder == null || emailTemplate == null) {
            return;
        }

        Email email = entityFactory.getEntity(Email.class);
        email.setToCustomer(entity);
        email.setFromCustomer(null);
        email.setFolder(defaultEmailFolder);
        email.setMediaType("text/html");
        email.setTitle("Confirm your email address");
        email.setTimeSent(Timestamp.from(Instant.now()));

        String tempToken = tokenGenerator.generateTempToken();
        String confirmUrl = getUrlConfirmEmail(tempToken);

        BindingModel bindingModel = BindingModel.getInstance();
        bindingModel.put("confirmUrl", confirmUrl);
        String processedEmail = emailTemplateProcessor.process(emailTemplate.getContent(), bindingModel);
        email.setContent(processedEmail);

        TemporaryToken token = entityFactory.getEntity(TemporaryToken.class);
        token.setAssignCustomer(entity);
        token.setValue(tempToken);
        Timestamp expiryTime = Timestamp.from(Instant.now(Clock.systemDefaultZone()).plus(1, ChronoUnit.DAYS));
        token.setExpiryTime(expiryTime);

        temporaryTokenRepository.saveTemporaryToken(token);
        customerRepository.updateCustomer(entity);
        emailRepository.saveEmail(email);
    }

    @Override
    protected void inject() {
        if (emailTemplateRepository == null) {
            emailTemplateRepository = resolveRepository(EmailTemplateRepository.class);
        }
        if (emailFoldersRepository == null) {
            emailFoldersRepository = resolveRepository(EmailFoldersRepository.class);
        }
        if (emailTemplateProcessor == null) {
            emailTemplateProcessor = resolveDependencies(EmailTemplateProcessor.class);
        }
        if (emailRepository == null) {
            emailRepository = resolveRepository(EmailRepository.class);
        }
        if (tokenGenerator == null) {
            tokenGenerator = resolveDependencies(TemporaryTokenGenerator.class);
        }
        if (temporaryTokenRepository == null) {
            temporaryTokenRepository = resolveRepository(TemporaryTokenRepository.class);
        }
        if (customerRepository == null) {
            customerRepository = resolveRepository(CustomerRepository.class);
        }
        if (entityFactory == null) {
            entityFactory = resolveDependencies(EntityFactory.class);
        }
    }

    private String getUrlConfirmEmail(String temporaryToken) {
        String confirmPath = getEnvironment().getProperty("shop.url.confirm-email");
        String serverAddress = getEnvironment().getProperty("server.address");
        String serverPort = getEnvironment().getProperty("server.port");
        String addressTemplate = "https://{0}:{1}{2}?token={3}";
        return MessageFormat.format(addressTemplate, serverAddress, serverPort, confirmPath, temporaryToken);
    }
}
