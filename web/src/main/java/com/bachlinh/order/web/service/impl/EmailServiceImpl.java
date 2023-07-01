package com.bachlinh.order.web.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.ServiceComponent;
import com.bachlinh.order.dto.DtoMapper;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Email;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.exception.system.common.CriticalException;
import com.bachlinh.order.exception.system.mail.MailException;
import com.bachlinh.order.mail.model.GmailMessage;
import com.bachlinh.order.mail.service.GmailSendingService;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.repository.EmailFoldersRepository;
import com.bachlinh.order.repository.EmailRepository;
import com.bachlinh.order.utils.JacksonUtils;
import com.bachlinh.order.utils.ResourceUtils;
import com.bachlinh.order.web.dto.form.admin.email.sending.NormalEmailSendingForm;
import com.bachlinh.order.web.dto.resp.EmailSendingResp;
import com.bachlinh.order.web.service.business.EmailSendingService;

import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;

@ServiceComponent
@ActiveReflection
@RequiredArgsConstructor(onConstructor = @__({@ActiveReflection, @DependenciesInitialize}))
public class EmailServiceImpl implements EmailSendingService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final GmailSendingService gmailSendingService;
    private final EmailRepository emailRepository;
    private final CustomerRepository customerRepository;
    private final EmailFoldersRepository emailFoldersRepository;
    private final EntityFactory entityFactory;
    private final DtoMapper dtoMapper;

    private String botEmail;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public EmailSendingResp sendNormalEmail(NormalEmailSendingForm form, Customer sender) {
        var email = entityFactory.getEntity(Email.class);
        email.setContent(form.getContent());
        email.setReceivedTime(Timestamp.from(Instant.now()));
        email.setTitle(form.getTitle());
        email.setRead(false);
        email.setSent(true);
        email.setMediaType(form.getContentType());
        var toCustomer = customerRepository.getCustomerById(form.getToCustomer(), false);
        email.setToCustomer(toCustomer);
        email.setFromCustomer(sender);
        var folder = emailFoldersRepository.getEmailFolderByName("Default", toCustomer);
        email.setFolder(folder);
        email.setTimeSent(Timestamp.from(Instant.now()));
        email = emailRepository.saveEmail(email);
        var gmailMessage = new GmailMessage(botEmail);
        gmailMessage.setCharset(StandardCharsets.UTF_8);
        gmailMessage.setContentType(email.getMediaType());
        gmailMessage.setToAddress(toCustomer.getEmail());
        gmailMessage.setSubject(email.getTitle());
        gmailMessage.setBody(email.getContent());
        var result = gmailSendingService.send(gmailMessage);
        if (result.getStatusCode() >= 400) {
            logger.error("Sending gmail failure", new MailException(result.getDetail()));
        }
        return dtoMapper.map(email, EmailSendingResp.class);
    }

    @DependenciesInitialize
    public void setUrlResetPassword(@Value("${active.profile}") String profile) {
        Environment environment = Environment.getInstance(profile);
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
}
