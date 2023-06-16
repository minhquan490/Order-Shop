package com.bachlinh.order.web.handler.rest.admin.category;

import lombok.NoArgsConstructor;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.exception.http.BadVariableException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.service.Form;
import com.bachlinh.order.web.dto.form.CategoryDeleteForm;
import com.bachlinh.order.web.dto.form.CategoryForm;
import com.bachlinh.order.web.service.common.CategoryService;

import java.util.HashMap;
import java.util.Map;

@RouteProvider
@ActiveReflection
@NoArgsConstructor(onConstructor_ = @ActiveReflection)
public class CategoryDeleteHandler extends AbstractController<Map<String, Object>, CategoryDeleteForm> {
    private CategoryService categoryService;
    private String url;

    @Override
    protected Map<String, Object> internalHandler(Payload<CategoryDeleteForm> request) {
        if (request.data().id().isBlank()) {
            throw new BadVariableException("Id of category must not be null");
        }
        var form = new CategoryForm();
        form.setId(request.data().id());
        categoryService.delete(Form.wrap(form));
        Map<String, Object> resp = new HashMap<>();
        resp.put("messages", new String[]{"OK"});
        resp.put("status", 200);
        return resp;
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
            url = getEnvironment().getProperty("shop.url.admin.category.delete");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.DELETE;
    }
}
