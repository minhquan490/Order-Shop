package com.bachlinh.order.web.dto.strategy;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.dto.strategy.AbstractDtoStrategy;
import com.bachlinh.order.dto.strategy.DtoStrategy;
import com.bachlinh.order.entity.model.EmailTemplate;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.resp.EmailTemplateInfoResp;

@ActiveReflection
public class EmailTemplateInfoStrategy extends AbstractDtoStrategy<EmailTemplateInfoResp, EmailTemplate> {

    @ActiveReflection
    private EmailTemplateInfoStrategy(DependenciesResolver dependenciesResolver, Environment environment) {
        super(dependenciesResolver, environment);
    }

    @Override
    protected void beforeConvert(EmailTemplate source, Class<EmailTemplateInfoResp> type) {
        // Do nothing
    }

    @Override
    protected EmailTemplateInfoResp doConvert(EmailTemplate source, Class<EmailTemplateInfoResp> type) {
        var resp = new EmailTemplateInfoResp();
        resp.setId(source.getId());
        resp.setTitle(source.getTitle());
        resp.setName(source.getName());
        resp.setContent(source.getContent());
        resp.setExpiryPolicy(source.getExpiryPolicy());
        resp.setTotalArgument(source.getTotalArgument());
        resp.setParams(source.getParams().split(","));
        return resp;
    }

    @Override
    protected void afterConvert(EmailTemplate source, Class<EmailTemplateInfoResp> type) {
        // Do nothing
    }

    @Override
    protected DtoStrategy<EmailTemplateInfoResp, EmailTemplate> createNew(DependenciesResolver dependenciesResolver, Environment environment) {
        return new EmailTemplateInfoStrategy(dependenciesResolver, environment);
    }

    @Override
    public Class<EmailTemplateInfoResp> getTargetType() {
        return EmailTemplateInfoResp.class;
    }
}
