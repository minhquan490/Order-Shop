package com.bachlinh.order.web.dto.strategy;

import org.checkerframework.checker.units.qual.Acceleration;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.dto.strategy.AbstractDtoStrategy;
import com.bachlinh.order.entity.model.EmailFolders;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.resp.EmailInfoInFolderListResp;

@ActiveReflection
public class EmailInfoListStrategy extends AbstractDtoStrategy<EmailInfoInFolderListResp, EmailFolders> {

    @Acceleration
    public EmailInfoListStrategy(DependenciesResolver dependenciesResolver, Environment environment) {
        super(dependenciesResolver, environment);
    }

    @Override
    protected void beforeConvert(EmailFolders source, Class<EmailInfoInFolderListResp> type) {
        // Do nothing
    }

    @Override
    protected EmailInfoInFolderListResp doConvert(EmailFolders source, Class<EmailInfoInFolderListResp> type) {
        var resp = new EmailInfoInFolderListResp();
        resp.setFolderId(source.getId());
        resp.setFolderName(source.getName());
        resp.setEmails(source.getEmails()
                .stream()
                .map(email -> {
                    var emailResp = new EmailInfoInFolderListResp.EmailInfo();
                    emailResp.setId(email.getId());
                    emailResp.setTitle(email.getTitle());
                    emailResp.setContent(email.getContent());
                    emailResp.setReceivedTime(email.getReceivedTime().toString());
                    emailResp.setMediaType(email.getMediaType());
                    emailResp.setSenderEmail(email.getFromCustomer().getEmail());
                    emailResp.setSenderName(String.join(" ", email.getFromCustomer().getLastName(), email.getFromCustomer().getFirstName()));
                    return emailResp;
                })
                .toList()
                .toArray(new EmailInfoInFolderListResp.EmailInfo[0]));
        return null;
    }

    @Override
    protected void afterConvert(EmailFolders source, Class<EmailInfoInFolderListResp> type) {
        // Do nothing
    }

    @Override
    public Class<EmailInfoInFolderListResp> getTargetType() {
        return EmailInfoInFolderListResp.class;
    }
}
