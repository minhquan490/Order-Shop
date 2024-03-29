package com.bachlinh.order.web.dto.rule;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DtoValidationRule;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.model.Category;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.web.repository.spi.CategoryRepository;
import com.bachlinh.order.web.repository.spi.MessageSettingRepository;
import com.bachlinh.order.core.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.admin.category.CategoryUpdateForm;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DtoValidationRule
@ActiveReflection
public class CategoryUpdateRule extends AbstractRule<CategoryUpdateForm> {
    private static final String NON_EMPTY_MESSAGE_ID = "MSG-000001";
    private static final String EXISTED_MESSAGE_ID = "MSG-000007";
    private static final String RANGE_INVALID_MESSAGE_ID = "MSG-000010";
    private static final String NON_IDENTITY_MESSAGE_ID = "MSG-000011";

    private static final String CATEGORY_NAME_KEY = "name";
    private CategoryRepository categoryRepository;
    private MessageSettingRepository messageSettingRepository;

    private CategoryUpdateRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    public AbstractRule<CategoryUpdateForm> getInstance(Environment environment, DependenciesResolver resolver) {
        return new CategoryUpdateRule(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(CategoryUpdateForm dto) {
        var r = new HashMap<String, List<String>>();

        if (!StringUtils.hasText(dto.id())) {
            MessageSetting nonIdentityMessage = messageSettingRepository.getMessageById(NON_IDENTITY_MESSAGE_ID);
            String errorContent = MessageFormat.format(nonIdentityMessage.getValue(), "category", "", "update");
            var key = "id";
            RuntimeUtils.computeMultiValueMap(key, errorContent, r);
            return createError(r);
        } else {
            if (categoryRepository.isExits(dto.id())) {
                var key = "id";
                RuntimeUtils.computeMultiValueMap(key, "Category is not existed", r);
            }
        }

        Category targetCategory = categoryRepository.getCategoryById(dto.id());

        if (!targetCategory.getName().equals(dto.name())) {
            validateName(dto.name(), r);
        }
        return createError(r);
    }

    @Override
    protected void injectDependencies() {
        if (categoryRepository == null) {
            categoryRepository = resolveRepository(CategoryRepository.class);
        }
        if (messageSettingRepository == null) {
            messageSettingRepository = resolveRepository(MessageSettingRepository.class);
        }
    }

    @Override
    public Class<CategoryUpdateForm> applyOnType() {
        return CategoryUpdateForm.class;
    }

    private ValidatedDto.ValidateResult createError(Map<String, List<String>> validationResult) {
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

    private void validateName(String name, Map<String, List<String>> validateResult) {
        String nameOfCategory = "Name of category";
        if (!StringUtils.hasText(name)) {
            MessageSetting nonEmptyMessage = messageSettingRepository.getMessageById(NON_EMPTY_MESSAGE_ID);
            RuntimeUtils.computeMultiValueMap(CATEGORY_NAME_KEY, MessageFormat.format(nonEmptyMessage.getValue(), nameOfCategory), validateResult);
        } else {
            if (name.length() < 4 || name.length() > 32) {
                MessageSetting rangeInvalidMessage = messageSettingRepository.getMessageById(RANGE_INVALID_MESSAGE_ID);
                RuntimeUtils.computeMultiValueMap(CATEGORY_NAME_KEY, MessageFormat.format(rangeInvalidMessage.getValue(), nameOfCategory, "4", "32"), validateResult);
            }

            if (categoryRepository.isCategoryNameExisted(name)) {
                MessageSetting existedMessage = messageSettingRepository.getMessageById(EXISTED_MESSAGE_ID);
                RuntimeUtils.computeMultiValueMap(CATEGORY_NAME_KEY, MessageFormat.format(existedMessage.getValue(), nameOfCategory), validateResult);
            }
        }
    }
}
