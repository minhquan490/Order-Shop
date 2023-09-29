package com.bachlinh.order.web.handler.rest.admin.product;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.EnableCsrf;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.Permit;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.admin.product.ProductDeleteMediaForm;
import com.bachlinh.order.web.service.common.ProductMediaService;
import org.apache.http.HttpStatus;

import java.util.Map;

@ActiveReflection
@RouteProvider
@Permit(roles = Role.ADMIN)
@EnableCsrf
public class ProductDeleteMediaHandler extends AbstractController<Map<String, Object>, ProductDeleteMediaForm> {

    private String url;
    private ProductMediaService productMediaService;

    private ProductDeleteMediaHandler() {
    }

    @Override
    public AbstractController<Map<String, Object>, ProductDeleteMediaForm> newInstance() {
        return new ProductDeleteMediaHandler();
    }

    @Override
    @ActiveReflection
    protected Map<String, Object> internalHandler(Payload<ProductDeleteMediaForm> request) {
        productMediaService.deleteMedia(request.data().getMediaUrl());
        return createDefaultResponse(HttpStatus.SC_OK, new String[]{"Delete successfully"});
    }

    @Override
    protected void inject() {
        if (productMediaService == null) {
            productMediaService = resolveService(ProductMediaService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.admin.product.media.delete");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.DELETE;
    }
}
