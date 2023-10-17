package com.bachlinh.order.web.dto.rule;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DtoValidationRule;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.web.repository.spi.CategoryRepository;
import com.bachlinh.order.web.repository.spi.MessageSettingRepository;
import com.bachlinh.order.core.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.admin.category.CategoryCreateForm;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class CategoryCreateRule extends AbstractRule<CategoryCreateForm> {
    private static final String CATEGORY_NAME_KEY = "name";
    private static final String NON_EMPTY_MESSAGE_ID = "MSG-000001";
    private static final String EXISTED_MESSAGE_ID = "MSG-000007";
    private static final String RANGE_INVALID_MESSAGE_ID = "MSG-000010";

    private CategoryRepository categoryRepository;
    private MessageSettingRepository messageSettingRepository;

    private CategoryCreateRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    public AbstractRule<CategoryCreateForm> getInstance(Environment environment, DependenciesResolver resolver) {
        return new CategoryCreateRule(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(CategoryCreateForm dto) {
        var r = new HashMap<String, List<String>>();

        if (!StringUtils.hasText(dto.name())) {
            MessageSetting nonEmptyMessage = messageSettingRepository.getMessageById(NON_EMPTY_MESSAGE_ID);
            RuntimeUtils.computeMultiValueMap(CATEGORY_NAME_KEY, MessageFormat.format(nonEmptyMessage.getValue(), "Name of category"), r);
        } else {
            if (dto.name().length() < 4 || dto.name().length() > 32) {
                MessageSetting rangeInvalidMessage = messageSettingRepository.getMessageById(RANGE_INVALID_MESSAGE_ID);
                RuntimeUtils.computeMultiValueMap(CATEGORY_NAME_KEY, MessageFormat.format(rangeInvalidMessage.getValue(), "Name of category", "4", "32"), r);
            }

            if (categoryRepository.isCategoryNameExisted(dto.name())) {
                MessageSetting existedMessage = messageSettingRepository.getMessageById(EXISTED_MESSAGE_ID);
                RuntimeUtils.computeMultiValueMap(CATEGORY_NAME_KEY, MessageFormat.format(existedMessage.getValue(), "Category"), r);
            }
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
            categoryRepository = resolveRepository(CategoryRepository.class);
        }
        if (messageSettingRepository == null) {
            messageSettingRepository = resolveRepository(MessageSettingRepository.class);
        }
    }

    @Override
    public Class<CategoryCreateForm> applyOnType() {
        return CategoryCreateForm.class;
    }
}
