package com.bachlinh.order.web.dto.strategy;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.dto.strategy.AbstractDtoStrategy;
import com.bachlinh.order.entity.model.Category;
import com.bachlinh.order.entity.model.Product;
import com.bachlinh.order.entity.model.ProductMedia;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.resp.AdminProductResp;

@ActiveReflection
public class AdminProductStrategy extends AbstractDtoStrategy<AdminProductResp, Product> {

    @ActiveReflection
    public AdminProductStrategy(DependenciesResolver dependenciesResolver, Environment environment) {
        super(dependenciesResolver, environment);
    }

    @Override
    protected void beforeConvert(Product source, Class<AdminProductResp> type) {
        // Do nothing
    }

    @Override
    protected AdminProductResp doConvert(Product source, Class<AdminProductResp> type) {
        AdminProductResp dto = new AdminProductResp();
        dto.setId(source.getId());
        dto.setName(source.getName());
        dto.setPrice(String.valueOf(source.getPrice()));
        dto.setSize(source.getSize());
        dto.setColor(source.getColor());
        dto.setTaobaoUrl(source.getTaobaoUrl());
        dto.setDescription(source.getDescription());
        dto.setOrderPoint(String.valueOf(source.getOrderPoint()));
        dto.setEnable(String.valueOf(source.isEnabled()));
        dto.setPictures(source.getMedias().stream().map(ProductMedia::getUrl).toList().toArray(new String[0]));
        dto.setCategories(source.getCategories().stream().map(Category::getName).toList().toArray(new String[0]));
        return dto;
    }

    @Override
    protected void afterConvert(Product source, Class<AdminProductResp> type) {
        // Do nothing
    }

    @Override
    public Class<AdminProductResp> getTargetType() {
        return AdminProductResp.class;
    }
}
