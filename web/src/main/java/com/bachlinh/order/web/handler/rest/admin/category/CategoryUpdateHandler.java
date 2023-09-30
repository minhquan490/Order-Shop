package com.bachlinh.order.web.handler.rest.admin.category;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.EnableCsrf;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.Permit;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.admin.category.CategoryUpdateForm;
import com.bachlinh.order.web.dto.resp.CategoryResp;
import com.bachlinh.order.web.service.common.CategoryService;

@RouteProvider(name = "categoryUpdateHandler")
@ActiveReflection
@Permit(roles = Role.ADMIN)
@EnableCsrf
public class CategoryUpdateHandler extends AbstractController<CategoryResp, CategoryUpdateForm> {
    private CategoryService categoryService;
    private String url;

    private CategoryUpdateHandler() {
    }

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
        if (categoryService == null) {
            categoryService = resolveService(CategoryService.class);
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
