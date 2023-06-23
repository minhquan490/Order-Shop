package com.bachlinh.order.web.handler.rest.admin.category;

import lombok.NoArgsConstructor;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.exception.http.BadVariableException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.service.Form;
import com.bachlinh.order.web.dto.form.CategoryCreateForm;
import com.bachlinh.order.web.dto.form.CategoryForm;
import com.bachlinh.order.web.dto.resp.CategoryResp;
import com.bachlinh.order.web.service.common.CategoryService;

@RouteProvider
@ActiveReflection
@NoArgsConstructor(onConstructor_ = @ActiveReflection)
public class CategoryCreateHandler extends AbstractController<CategoryResp, CategoryCreateForm> {
    private CategoryService categoryService;
    private String url;

    @Override
    protected CategoryResp internalHandler(Payload<CategoryCreateForm> request) {
        var name = request.data().name();
        if (name == null || name.isBlank()) {
            throw new BadVariableException("Name of category must not be empty");
        }
        var form = new CategoryForm();
        form.setName(name);
        var result = categoryService.save(Form.wrap(form));
        return result.get();
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
