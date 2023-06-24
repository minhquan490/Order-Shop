package com.bachlinh.order.web.rule;

import org.springframework.util.StringUtils;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DtoValidationRule;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.repository.CategoryRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.CategoryCreateForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class CategoryCreateRule extends AbstractRule<CategoryCreateForm> {
    private CategoryRepository categoryRepository;

    @ActiveReflection
    public CategoryCreateRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(CategoryCreateForm dto) {
        var r = new HashMap<String, List<String>>();
        if (!StringUtils.hasText(dto.name())) {
            var key = "name";
            RuntimeUtils.computeMultiValueMap(key, "Name of category must not be empty", r);
        }
        if (categoryRepository.getCategoryByName(dto.name()) != null) {
            var key = "name";
            RuntimeUtils.computeMultiValueMap(key, "Category is existed", r);
        }
        return new ValidatedDto.ValidateResult() {
            @Override
            public Map<String, Object> getErrorResult() {
                return new HashMap<>(r);
            }

            @Override
            public boolean shouldHandle() {
                return r.isEmpty();
            }
        };
    }

    @Override
    protected void injectDependencies() {
        if (categoryRepository == null) {
            categoryRepository = getResolver().resolveDependencies(CategoryRepository.class);
        }
    }

    @Override
    public Class<CategoryCreateForm> applyOnType() {
        return CategoryCreateForm.class;
    }
}
