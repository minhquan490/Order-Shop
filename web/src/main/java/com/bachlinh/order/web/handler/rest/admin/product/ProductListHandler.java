package com.bachlinh.order.web.handler.rest.admin.product;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.annotation.Transactional;
import com.bachlinh.order.core.enums.Isolation;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.core.annotation.Permit;
import com.bachlinh.order.core.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.AdminProductResp;
import com.bachlinh.order.web.service.common.ProductService;

import java.util.Collection;

@RouteProvider
@ActiveReflection
@Permit(roles = Role.ADMIN)
public class ProductListHandler extends AbstractController<Collection<AdminProductResp>, Void> {
    private String url;
    private ProductService productService;

    private ProductListHandler() {
    }

    @Override
    public AbstractController<Collection<AdminProductResp>, Void> newInstance() {
        return new ProductListHandler();
    }

    @Override
    @ActiveReflection
    @Transactional(isolation = Isolation.READ_COMMITTED, timeOut = 10)
    protected Collection<AdminProductResp> internalHandler(Payload<Void> request) {
        String page = getNativeRequest().getUrlQueryParam().getFirst("page");
        return productService.getProducts(page);
    }

    @Override
    protected void inject() {
        if (productService == null) {
            productService = resolveService(ProductService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.admin.product.list");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
