package com.bachlinh.order.web.rule;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DtoValidationRule;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.repository.CategoryRepository;
import com.bachlinh.order.repository.ProductRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.admin.ProductCreateForm;

@ActiveReflection
@DtoValidationRule
public class ProductCreateRule extends AbstractRule<ProductCreateForm> {
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;

    @ActiveReflection
    public ProductCreateRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(ProductCreateForm dto) {

        return null;
    }

    @Override
    protected void injectDependencies() {
        if (productRepository == null) {
            productRepository = getResolver().resolveDependencies(ProductRepository.class);
        }
        if (categoryRepository == null) {
            categoryRepository = getResolver().resolveDependencies(CategoryRepository.class);
        }
    }

    @Override
    public Class<ProductCreateForm> applyOnType() {
        return ProductCreateForm.class;
    }
}
