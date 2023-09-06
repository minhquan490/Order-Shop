package com.bachlinh.order.web.handler.rest.admin.category;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.EnableCsrf;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.Permit;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.admin.category.CategoryCreateForm;
import com.bachlinh.order.web.dto.resp.CategoryResp;
import com.bachlinh.order.web.service.common.CategoryService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@RouteProvider(name = "categoryCreateHandler")
@ActiveReflection
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Permit(roles = Role.ADMIN)
@EnableCsrf
public class CategoryCreateHandler extends AbstractController<CategoryResp, CategoryCreateForm> {
    private CategoryService categoryService;
    private String url;

    @Override
    public AbstractController<CategoryResp, CategoryCreateForm> newInstance() {
        return new CategoryCreateHandler();
    }

    @Override
    @ActiveReflection
    protected CategoryResp internalHandler(Payload<CategoryCreateForm> request) {
        return categoryService.saveCategory(request.data());
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
            url = getEnvironment().getProperty("shop.url.admin.category.create");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.POST;
    }
}
