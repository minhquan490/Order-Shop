package com.bachlinh.order.web.handler.rest.common;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.http.Payload;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.CategoryResp;
import com.bachlinh.order.web.service.common.CategoryService;

import java.util.Collection;

@RouteProvider(name = "categoryListHandler")
@ActiveReflection
public class CategoryListHandler extends AbstractController<Collection<CategoryResp>, Void> {
    private CategoryService categoryService;
    private String url;

    private CategoryListHandler() {
        super();
    }

    @Override
    public AbstractController<Collection<CategoryResp>, Void> newInstance() {
        return new CategoryListHandler();
    }

    @Override
    @ActiveReflection
    protected Collection<CategoryResp> internalHandler(Payload<Void> request) {
        return categoryService.getCategories();
    }

    @Override
    protected void inject() {
        if (categoryService == null) {
            categoryService = resolveService(CategoryService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.content.category.list");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
