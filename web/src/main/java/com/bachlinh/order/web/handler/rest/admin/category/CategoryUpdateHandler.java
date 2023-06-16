package com.bachlinh.order.web.handler.rest.admin.category;

import lombok.NoArgsConstructor;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.exception.http.BadVariableException;
import com.bachlinh.order.exception.http.ResourceNotFoundException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.service.Form;
import com.bachlinh.order.web.dto.form.CategoryForm;
import com.bachlinh.order.web.dto.form.CategoryUpdateForm;
import com.bachlinh.order.web.dto.resp.CategoryResp;
import com.bachlinh.order.web.service.common.CategoryService;

@RouteProvider
@ActiveReflection
@NoArgsConstructor(onConstructor_ = @ActiveReflection)
public class CategoryUpdateHandler extends AbstractController<CategoryResp, CategoryUpdateForm> {
    private CategoryService categoryService;
    private String url;

    @Override
    protected CategoryResp internalHandler(Payload<CategoryUpdateForm> request) {
        var req = request.data();
        if (categoryService.isExist(req.id())) {
            throw new ResourceNotFoundException("Category with id: [" + req.id() + "]", getPath());
        }
        if (req.name().isBlank()) {
            throw new BadVariableException("Name of category must be not empty");
        }
        var form = new CategoryForm();
        form.setId(req.id());
        form.setName(req.name());
        var result = categoryService.update(Form.wrap(form));
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
            url = getEnvironment().getProperty("shop.url.admin.category.update");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.PUT;
    }
}
