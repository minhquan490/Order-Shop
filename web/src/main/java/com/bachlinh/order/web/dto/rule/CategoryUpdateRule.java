package com.bachlinh.order.web.dto.rule;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DtoValidationRule;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.repository.CategoryRepository;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.RuntimeUtils;
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

    @ActiveReflection
    public CategoryUpdateRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
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

        String nameOfCategory = "Name of category";
        if (!StringUtils.hasText(dto.name())) {
            MessageSetting nonEmptyMessage = messageSettingRepository.getMessageById(NON_EMPTY_MESSAGE_ID);
            RuntimeUtils.computeMultiValueMap(CATEGORY_NAME_KEY, MessageFormat.format(nonEmptyMessage.getValue(), nameOfCategory), r);
        } else {
            if (dto.name().length() < 4 || dto.name().length() > 32) {
                MessageSetting rangeInvalidMessage = messageSettingRepository.getMessageById(RANGE_INVALID_MESSAGE_ID);
                RuntimeUtils.computeMultiValueMap(CATEGORY_NAME_KEY, MessageFormat.format(rangeInvalidMessage.getValue(), nameOfCategory, "4", "32"), r);
            }

            if (categoryRepository.getCategoryByName(dto.name()) != null) {
                MessageSetting existedMessage = messageSettingRepository.getMessageById(EXISTED_MESSAGE_ID);
                RuntimeUtils.computeMultiValueMap(CATEGORY_NAME_KEY, MessageFormat.format(existedMessage.getValue(), nameOfCategory), r);
            }
        }
        return createError(r);
    }

    @Override
    protected void injectDependencies() {
        if (categoryRepository == null) {
            categoryRepository = getResolver().resolveDependencies(CategoryRepository.class);
        }
        if (messageSettingRepository == null) {
            messageSettingRepository = getResolver().resolveDependencies(MessageSettingRepository.class);
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
}
