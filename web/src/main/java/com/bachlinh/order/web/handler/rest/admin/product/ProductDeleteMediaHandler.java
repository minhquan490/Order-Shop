package com.bachlinh.order.web.handler.rest.admin.product;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.EnableCsrf;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.annotation.Transactional;
import com.bachlinh.order.core.enums.Isolation;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.core.annotation.Permit;
import com.bachlinh.order.core.enums.Role;
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
        super();
    }

    @Override
    public AbstractController<Map<String, Object>, ProductDeleteMediaForm> newInstance() {
        return new ProductDeleteMediaHandler();
    }

    @Override
    @ActiveReflection
    @Transactional(isolation = Isolation.READ_COMMITTED, timeOut = 10)
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
