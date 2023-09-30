package com.bachlinh.order.web.dto.strategy;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.dto.strategy.AbstractDtoStrategy;
import com.bachlinh.order.dto.strategy.DtoStrategy;
import com.bachlinh.order.entity.model.EmailTrash;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.web.dto.resp.EmailTrashResp;

import java.util.Objects;

@ActiveReflection
public class EmailTrashStrategy extends AbstractDtoStrategy<EmailTrashResp, EmailTrash> {

    @ActiveReflection
    private EmailTrashStrategy(DependenciesResolver dependenciesResolver, Environment environment) {
        super(dependenciesResolver, environment);
    }

    @Override
    protected void beforeConvert(EmailTrash source, Class<EmailTrashResp> type) {
        // Do nothing
    }

    @Override
    protected EmailTrashResp doConvert(EmailTrash source, Class<EmailTrashResp> type) {
        var resp = new EmailTrashResp();
        resp.setId(Objects.requireNonNull(source.getId()).toString());
        resp.setEmails(source.getEmails()
                .stream()
                .map(email -> {
                    var respEmail = new EmailTrashResp.Email();
                    respEmail.setId(email.getId());
                    respEmail.setTitle(email.getTitle());
                    respEmail.setContent(email.getContent());
                    return respEmail;
                })
                .toList()
                .toArray(new EmailTrashResp.Email[0]));
        return resp;
    }

    @Override
    protected void afterConvert(EmailTrash source, Class<EmailTrashResp> type) {
        // Do nothing
    }

    @Override
    protected DtoStrategy<EmailTrashResp, EmailTrash> createNew(DependenciesResolver dependenciesResolver, Environment environment) {
        return new EmailTrashStrategy(dependenciesResolver, environment);
    }

    @Override
    public Class<EmailTrashResp> getTargetType() {
        return EmailTrashResp.class;
    }
}
