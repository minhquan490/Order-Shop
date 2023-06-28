package com.bachlinh.order.web.handler.rest.admin.category;

import lombok.NoArgsConstructor;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.exception.system.common.CriticalException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.CategoryDeleteForm;
import com.bachlinh.order.web.service.common.CategoryService;

import java.util.HashMap;
import java.util.Map;

@RouteProvider
@ActiveReflection
@NoArgsConstructor(onConstructor = @__({@ActiveReflection}))
public class CategoryDeleteHandler extends AbstractController<Map<String, Object>, CategoryDeleteForm> {
    private CategoryService categoryService;
    private String url;

    @Override
    @ActiveReflection
    protected Map<String, Object> internalHandler(Payload<CategoryDeleteForm> request) {
        var result = categoryService.deleteCategory(request.data());
        if (result) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("messages", new String[]{"OK"});
            resp.put("status", 200);
            return resp;
        } else {
            throw new CriticalException("Server under maintenance");
        }
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
