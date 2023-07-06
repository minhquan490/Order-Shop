package com.bachlinh.order.web.dto.rule;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DtoValidationRule;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.common.DeleteEmailInTrashForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class DeleteEmailInTrashRule extends AbstractRule<DeleteEmailInTrashForm> {

    @ActiveReflection
    public DeleteEmailInTrashRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(DeleteEmailInTrashForm dto) {
        var validationResult = new HashMap<String, List<String>>(1);
        if (dto.getEmailId().length == 0) {
            var key = "email_ids";
            RuntimeUtils.computeMultiValueMap(key, "Need at least 1 email for delete", validationResult);
        }
        return new ValidatedDto.ValidateResult() {
            @Override
            public Map<String, Object> getErrorResult() {
                return new HashMap<>(validationResult);
            }

            @Override
            public boolean shouldHandle() {
                return validationResult.isEmpty();
            }
        };
    }

    @Override
    protected void injectDependencies() {
        // Do nothing
    }

    @Override
    public Class<DeleteEmailInTrashForm> applyOnType() {
        return DeleteEmailInTrashForm.class;
    }
}
