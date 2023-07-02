package com.bachlinh.order.web.dto.rule;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.common.AddEmailToTrashForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
public class AddEMailToTrashRule extends AbstractRule<AddEmailToTrashForm> {

    @ActiveReflection
    public AddEMailToTrashRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(AddEmailToTrashForm dto) {
        var validateResult = new HashMap<String, List<String>>();

        if (dto.getEmailIds().length == 0) {
            var key = "email_ids";
            RuntimeUtils.computeMultiValueMap(key, "Can not add unknown email to trash", validateResult);
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
    public Class<AddEmailToTrashForm> applyOnType() {
        return AddEmailToTrashForm.class;
    }
}
