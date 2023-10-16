package com.bachlinh.order.web.handler.rest.admin.category;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.EnableCsrf;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.annotation.Transactional;
import com.bachlinh.order.core.enums.Isolation;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.exception.system.common.CriticalException;
import com.bachlinh.order.http.Payload;
import com.bachlinh.order.core.annotation.Permit;
import com.bachlinh.order.core.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.admin.category.CategoryDeleteForm;
import com.bachlinh.order.web.service.common.CategoryService;

import java.util.Map;

@RouteProvider(name = "categoryDeleteHandler")
@ActiveReflection
@Permit(roles = Role.ADMIN)
@EnableCsrf
public class CategoryDeleteHandler extends AbstractController<Map<String, Object>, CategoryDeleteForm> {
    private CategoryService categoryService;
    private String url;

    private CategoryDeleteHandler() {
        super();
    }

    @Override
    public AbstractController<Map<String, Object>, CategoryDeleteForm> newInstance() {
        return new CategoryDeleteHandler();
    }

    @Override
    @ActiveReflection
    @Transactional(isolation = Isolation.READ_COMMITTED, timeOut = 10)
    protected Map<String, Object> internalHandler(Payload<CategoryDeleteForm> request) {
        var result = categoryService.deleteCategory(request.data());
        if (result) {
            return createDefaultResponse(200, new String[]{"OK"});
        } else {
            throw new CriticalException("Server under maintenance");
        }
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
            url = getEnvironment().getProperty("shop.url.admin.category.delete");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.DELETE;
    }
}
