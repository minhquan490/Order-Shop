package com.bachlinh.order.web.dto.rule;

import org.springframework.util.StringUtils;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DtoValidationRule;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.repository.CategoryRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.admin.category.CategoryCreateForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class CategoryCreateRule extends AbstractRule<CategoryCreateForm> {
    private static final String CATEGORY_NAME_KEY = "name";

    private CategoryRepository categoryRepository;

    @ActiveReflection
    public CategoryCreateRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(CategoryCreateForm dto) {
        var r = new HashMap<String, List<String>>();
        if (!StringUtils.hasText(dto.name())) {
            RuntimeUtils.computeMultiValueMap(CATEGORY_NAME_KEY, "Name of category must not be empty", r);
        }
        if (dto.name().length() < 4 || dto.name().length() > 32) {
            RuntimeUtils.computeMultiValueMap(CATEGORY_NAME_KEY, "Name of category must be in range 4 - 32", r);
        }
        if (categoryRepository.getCategoryByName(dto.name()) != null) {
            RuntimeUtils.computeMultiValueMap(CATEGORY_NAME_KEY, "Category is existed", r);
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
