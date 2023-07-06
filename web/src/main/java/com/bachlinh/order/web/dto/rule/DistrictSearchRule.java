package com.bachlinh.order.web.dto.rule;

import org.springframework.util.StringUtils;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DtoValidationRule;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.common.DistrictSearchForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class DistrictSearchRule extends AbstractRule<DistrictSearchForm> {

    @ActiveReflection
    public DistrictSearchRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(DistrictSearchForm dto) {
        var validationResult = new HashMap<String, List<String>>();
        if (!StringUtils.hasText(dto.getQuery())) {
            var key = "query";
            RuntimeUtils.computeMultiValueMap(key, "Can not perform searching with empty keyword", validationResult);
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
    public Class<DistrictSearchForm> applyOnType() {
        return DistrictSearchForm.class;
    }
}
