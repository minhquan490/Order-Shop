package com.bachlinh.order.web.handler.rest.admin.category;

import lombok.NoArgsConstructor;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.CategoryResp;
import com.bachlinh.order.web.service.common.CategoryService;

import java.util.Collection;

@RouteProvider
@ActiveReflection
@NoArgsConstructor(onConstructor = @__({@ActiveReflection}))
public class CategoryListHandler extends AbstractController<Collection<CategoryResp>, Void> {
    private CategoryService categoryService;
    private String url;

    @Override
    @ActiveReflection
    protected Collection<CategoryResp> internalHandler(Payload<Void> request) {
        return categoryService.getCategories();
    }

    @Override
    protected void inject() {
        var resolver = getContainerResolver().getDependenciesResolver();
        if (categoryService == null) {
            categoryService = resolver.resolveDependencies(CategoryService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.admin.category.list");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
