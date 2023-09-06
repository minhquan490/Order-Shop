package com.bachlinh.order.web.handler.rest.admin.category;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.EnableCsrf;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.Permit;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.admin.category.CategoryUpdateForm;
import com.bachlinh.order.web.dto.resp.CategoryResp;
import com.bachlinh.order.web.service.common.CategoryService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@RouteProvider(name = "categoryUpdateHandler")
@ActiveReflection
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Permit(roles = Role.ADMIN)
@EnableCsrf
public class CategoryUpdateHandler extends AbstractController<CategoryResp, CategoryUpdateForm> {
    private CategoryService categoryService;
    private String url;

    @Override
    public AbstractController<CategoryResp, CategoryUpdateForm> newInstance() {
        return new CategoryUpdateHandler();
    }

    @Override
    @ActiveReflection
    protected CategoryResp internalHandler(Payload<CategoryUpdateForm> request) {
        return categoryService.updateCategory(request.data());
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
            url = getEnvironment().getProperty("shop.url.admin.category.update");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.PATCH;
    }
}
