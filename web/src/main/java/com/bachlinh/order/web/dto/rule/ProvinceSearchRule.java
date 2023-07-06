package com.bachlinh.order.web.dto.rule;

import org.springframework.util.StringUtils;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DtoValidationRule;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.common.ProvinceSearchForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class ProvinceSearchRule extends AbstractRule<ProvinceSearchForm> {

    @ActiveReflection
    public ProvinceSearchRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(ProvinceSearchForm dto) {
        var validationResult = new HashMap<String, List<String>>();

        if (!StringUtils.hasText(dto.getQuery())) {
            var key = "query";
            RuntimeUtils.computeMultiValueMap(key, "Can not perform searching for empty keyword", validationResult);
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
    public Class<ProvinceSearchForm> applyOnType() {
        return ProvinceSearchForm.class;
    }
}
