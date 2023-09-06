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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@ActiveReflection
@RouteProvider
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Permit(roles = Role.ADMIN)
@EnableCsrf
public class ProductDeleteMediaHandler extends AbstractController<Void, ProductDeleteMediaForm> {
    private static final Void RETURN_INSTANCE = initReturnObject();

    private String url;
    private ProductMediaService productMediaService;

    @Override
    public AbstractController<Void, ProductDeleteMediaForm> newInstance() {
        return new ProductDeleteMediaHandler();
    }

    @Override
    @ActiveReflection
    protected Void internalHandler(Payload<ProductDeleteMediaForm> request) {
        productMediaService.deleteMedia(request.data().getMediaUrl());
        return RETURN_INSTANCE;
    }

    @Override
    protected void inject() {
        var resolver = getContainerResolver().getDependenciesResolver();
        if (productMediaService == null) {
            productMediaService = resolver.resolveDependencies(ProductMediaService.class);
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

    private static Void initReturnObject() {
        try {
            return Void.class.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return null;
        }
    }
}
