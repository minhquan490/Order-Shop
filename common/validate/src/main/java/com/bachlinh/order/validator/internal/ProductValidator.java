package com.bachlinh.order.validator.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.Product;
import com.bachlinh.order.repository.ProductRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.validator.spi.AbstractValidator;
import com.bachlinh.order.validator.spi.Result;

@ActiveReflection
public class ProductValidator extends AbstractValidator<Product> {
    private ProductRepository productRepository;

    @ActiveReflection
    public ProductValidator(DependenciesResolver resolver) {
        super(resolver);
    }

    @Override
    protected void inject() {
        if (productRepository == null) {
            productRepository = getResolver().resolveDependencies(ProductRepository.class);
        }
    }

    @Override
    protected ValidateResult doValidate(Product entity) {
        Result result = new Result();
        if (entity.getName().length() > 100) {
            result.addMessageError("Product name: is greater than 100 character");
        }
        if (entity.getName().isBlank()) {
            result.addMessageError("Product name: is blank");
        }
        if (productRepository.productNameExist(entity)) {
            result.addMessageError("Product name: is exist");
        }
        if (entity.getSize().length() > 3) {
            result.addMessageError("Product size: is greater than 3 character");
        }
        if (entity.getSize().isBlank()) {
            result.addMessageError("Product size: is blank");
        }
        if (entity.getColor().length() > 30) {
            result.addMessageError("Product color: is greater than 30 character");
        }
        if (entity.getColor().isBlank()) {
            result.addMessageError("Product color: is blank");
        }
        if (entity.getPrice() < 0) {
            result.addMessageError("Price: must be negative");
        }
        if (entity.getCategories() == null || entity.getCategories().isEmpty()) {
            result.addMessageError("Categories: product missing category");
        }
        return result;
    }
}
