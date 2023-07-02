package com.bachlinh.order.web.dto.rule;

import org.springframework.util.StringUtils;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DtoValidationRule;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.admin.email.sending.NormalEmailSendingForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class EmailSendingRule extends AbstractRule<NormalEmailSendingForm> {

    public EmailSendingRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(NormalEmailSendingForm dto) {
        var validateResult = new HashMap<String, List<String>>();
        if (!StringUtils.hasText(dto.getContent())) {
            var key = "content";
            RuntimeUtils.computeMultiValueMap(key, "Email content must not be empty", validateResult);
        }
        if (!StringUtils.hasText(dto.getTitle())) {
            var key = "title";
            RuntimeUtils.computeMultiValueMap(key, "Email title must not be empty", validateResult);
        }
        if (!StringUtils.hasText(dto.getToCustomer())) {
            var key = "to";
            RuntimeUtils.computeMultiValueMap(key, "Receiver must be specify", validateResult);
        }
        if (!StringUtils.hasText(dto.getContentType())) {
            var key = "content_type";
            RuntimeUtils.computeMultiValueMap(key, "Content type of email must not be empty", validateResult);
        }
        return new ValidatedDto.ValidateResult() {
            @Override
            public Map<String, Object> getErrorResult() {
                return new HashMap<>(validateResult);
            }

            @Override
            public boolean shouldHandle() {
                return validateResult.isEmpty();
            }
        };
    }

    @Override
    protected void injectDependencies() {
        // Do nothing
    }

    @Override
    public Class<NormalEmailSendingForm> applyOnType() {
        return NormalEmailSendingForm.class;
    }
}
