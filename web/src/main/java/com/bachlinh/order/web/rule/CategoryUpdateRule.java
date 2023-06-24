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
import com.bachlinh.order.web.dto.form.CategoryUpdateForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DtoValidationRule
@ActiveReflection
public class CategoryUpdateRule extends AbstractRule<CategoryUpdateForm> {
    private CategoryRepository categoryRepository;

    @ActiveReflection
    public CategoryUpdateRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(CategoryUpdateForm dto) {
        var r = new HashMap<String, List<String>>();

        if (!StringUtils.hasText(dto.id())) {
            var key = "id";
            RuntimeUtils.computeMultiValueMap(key, "Can not identity category for update", r);
        }

        if (!StringUtils.hasText(dto.name())) {
            var key = "name";
            RuntimeUtils.computeMultiValueMap(key, "Name of category must not be empty", r);
        }

        if (categoryRepository.isExits(dto.id())) {
            var key = "id";
            RuntimeUtils.computeMultiValueMap(key, "Category is not existed", r);
        }

        if (categoryRepository.getCategoryByName(dto.name()) != null) {
            var key = "name";
            RuntimeUtils.computeMultiValueMap(key, "Name of category is existed", r);
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
    public Class<CategoryUpdateForm> applyOnType() {
        return CategoryUpdateForm.class;
    }
}
