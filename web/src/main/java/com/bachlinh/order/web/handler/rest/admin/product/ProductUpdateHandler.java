package com.bachlinh.order.web.handler.rest.admin.product;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.EnableCsrf;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.core.annotation.Permit;
import com.bachlinh.order.core.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.admin.product.ProductUpdateForm;
import com.bachlinh.order.web.dto.resp.ProductResp;
import com.bachlinh.order.web.service.common.ProductService;

@RouteProvider(name = "productUpdateHandler")
@ActiveReflection
@Permit(roles = Role.ADMIN)
@EnableCsrf
public class ProductUpdateHandler extends AbstractController<ProductResp, ProductUpdateForm> {
    private String productUpdateUrl;
    private ProductService productService;

    private ProductUpdateHandler() {
        super();
    }

    @Override
    public AbstractController<ProductResp, ProductUpdateForm> newInstance() {
        return new ProductUpdateHandler();
    }

    @Override
    @ActiveReflection
    protected ProductResp internalHandler(Payload<ProductUpdateForm> request) {
        return productService.updateProduct(request.data());
    }

    @Override
    protected void inject() {
        if (productService == null) {
            productService = resolveService(ProductService.class);
        }
    }

    @Override
    public String getPath() {
        if (productUpdateUrl == null) {
            productUpdateUrl = getEnvironment().getProperty("shop.url.admin.product.update");
        }
        return productUpdateUrl;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.PATCH;
    }
}
