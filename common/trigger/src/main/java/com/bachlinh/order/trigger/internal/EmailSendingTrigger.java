package com.bachlinh.order.trigger.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.ApplyOn;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.entity.enums.TriggerExecution;
import com.bachlinh.order.entity.enums.TriggerMode;
import com.bachlinh.order.entity.model.Email;
import com.bachlinh.order.exception.system.common.CriticalException;
import com.bachlinh.order.exception.system.mail.MailException;
import com.bachlinh.order.mail.model.GmailMessage;
import com.bachlinh.order.mail.service.GmailSendingService;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.trigger.spi.AbstractTrigger;
import com.bachlinh.order.utils.JacksonUtils;
import com.bachlinh.order.utils.ResourceUtils;

import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;

@ActiveReflection
@ApplyOn(entity = Email.class)
public class EmailSendingTrigger extends AbstractTrigger<Email> {

    private GmailSendingService gmailSendingService;
    private String botEmail;

    @ActiveReflection
    public EmailSendingTrigger(DependenciesResolver dependenciesResolver) {
        super(dependenciesResolver);
    }

    @Override
    public TriggerMode getMode() {
        return TriggerMode.AFTER;
    }

    @Override
    public TriggerExecution[] getExecuteOn() {
        return new TriggerExecution[]{TriggerExecution.ON_INSERT};
    }

    @Override
    protected void doExecute(Email entity) {
        if (entity.getFromCustomer() == null || entity.getFromCustomer().getRole().equals(Role.ADMIN.name())) {
            var gmailMessage = new GmailMessage(botEmail);
            gmailMessage.setCharset(StandardCharsets.UTF_8);
            gmailMessage.setContentType(entity.getMediaType());
            gmailMessage.setToAddress(entity.getToCustomer().getEmail());
            gmailMessage.setSubject(entity.getTitle());
            gmailMessage.setBody(entity.getContent());
            var result = gmailSendingService.send(gmailMessage);
            if (result.getStatusCode() >= 400) {
                log.error("Sending gmail failure", new MailException(result.getDetail()));
            }
        }
    }

    @Override
    protected void inject() {
        if (gmailSendingService == null) {
            gmailSendingService = getDependenciesResolver().resolveDependencies(GmailSendingService.class);
        }
        if (botEmail == null) {
            try {
                var node = JacksonUtils.readJsonFile(ResourceUtils.getURL(getEnvironment().getProperty("google.email.credentials")));
                this.botEmail = node.get("client_email").asText();
                if (this.botEmail.isEmpty()) {
                    throw new CriticalException("Google credentials json not valid");
                }
            } catch (FileNotFoundException e) {
                throw new CriticalException("Google credentials not found", e);
            }
        }
    }

    @Override
    public String getTriggerName() {
        return "gmailSendingTrigger";
    }
}
