package com.bachlinh.order.validate.validator.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.CrawlResult;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.validate.validator.spi.AbstractValidator;
import com.bachlinh.order.validate.validator.spi.Result;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;

@ActiveReflection
public class CrawlResultValidator extends AbstractValidator<CrawlResult> {
    private static final String NON_EMPTY_MESSAGE_ID = "MSG-000001";
    private static final String LENGTH_INVALID_MESSAGE_ID = "MSG-000002";
    private static final String NON_NULL_MESSAGE_ID = "MSG-000003";

    private MessageSettingRepository messageSettingRepository;

    @ActiveReflection
    public CrawlResultValidator(DependenciesResolver resolver) {
        super(resolver);
    }

    @Override
    protected void inject() {
        if (messageSettingRepository == null) {
            messageSettingRepository = getResolver().resolveDependencies(MessageSettingRepository.class);
        }
    }

    @Override
    protected ValidateResult doValidate(CrawlResult entity) {
        Result result = new Result();

        MessageSetting nonEmptyMessage = messageSettingRepository.getMessageById(NON_EMPTY_MESSAGE_ID);
        MessageSetting lengthInvalidMessage = messageSettingRepository.getMessageById(LENGTH_INVALID_MESSAGE_ID);
        MessageSetting nonNullMessage = messageSettingRepository.getMessageById(NON_NULL_MESSAGE_ID);

        if (!StringUtils.hasText(entity.getSourcePath())) {
            result.addMessageError(MessageFormat.format(nonEmptyMessage.getValue(), "Crawl result source path"));
        }

        if (entity.getSourcePath().length() > 100) {
            result.addMessageError(MessageFormat.format(lengthInvalidMessage.getValue(), "crawl result source path", "100"));
        }

        if (entity.getTimeFinish() == null) {
            result.addMessageError(MessageFormat.format(nonNullMessage.getValue(), "Time finish"));
        }

        if (entity.getResources() != null && entity.getResources().length() > 1000) {
            result.addMessageError(MessageFormat.format(lengthInvalidMessage.getValue(), "Resource content", "1000"));
        }

        return result;
    }
}
