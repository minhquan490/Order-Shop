package com.bachlinh.order.web.dto.strategy;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.dto.strategy.AbstractDtoStrategy;
import com.bachlinh.order.entity.model.EmailTemplateFolder;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.resp.EmailTemplateFolderInfoResp;

@ActiveReflection
public class EmailTemplateFolderInfoRespStrategy extends AbstractDtoStrategy<EmailTemplateFolderInfoResp, EmailTemplateFolder> {

    @ActiveReflection
    public EmailTemplateFolderInfoRespStrategy(DependenciesResolver dependenciesResolver, Environment environment) {
        super(dependenciesResolver, environment);
    }

    @Override
    protected void beforeConvert(EmailTemplateFolder source, Class<EmailTemplateFolderInfoResp> type) {
        // Do nothing
    }

    @Override
    protected EmailTemplateFolderInfoResp doConvert(EmailTemplateFolder source, Class<EmailTemplateFolderInfoResp> type) {
        var result = new EmailTemplateFolderInfoResp();
        result.setId(source.getId());
        result.setName(source.getName());
        result.setTimeTemplateCleared(source.getClearTemplatePolicy().toString());
        if (!source.getEmailTemplates().isEmpty()) {
            result.setEmails(source.getEmailTemplates()
                    .stream()
                    .map(emailTemplate -> {
                        var email = new EmailTemplateFolderInfoResp.EmailTemplateResp();
                        email.setId(emailTemplate.getId());
                        email.setName(emailTemplate.getName());
                        email.setTitle(emailTemplate.getTitle());
                        return email;
                    })
                    .toList()
                    .toArray(new EmailTemplateFolderInfoResp.EmailTemplateResp[0])
            );
            result.setTotalEmail(String.valueOf(result.getEmails().length));
        }
        return result;
    }

    @Override
    protected void afterConvert(EmailTemplateFolder source, Class<EmailTemplateFolderInfoResp> type) {
        // Do nothing
    }

    @Override
    public Class<EmailTemplateFolderInfoResp> getTargetType() {
        return EmailTemplateFolderInfoResp.class;
    }
}
