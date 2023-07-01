package com.bachlinh.order.web.handler.rest.admin.product;

import lombok.NoArgsConstructor;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.form.admin.product.ProductUpdateForm;
import com.bachlinh.order.web.dto.resp.ProductResp;
import com.bachlinh.order.web.service.common.ProductService;

@RouteProvider
@ActiveReflection
@NoArgsConstructor(onConstructor = @__({@ActiveReflection}))
public class ProductUpdateHandler extends AbstractController<ProductResp, ProductUpdateForm> {
    private String productUpdateUrl;
    private ProductService productService;

    @Override
    @ActiveReflection
    protected ProductResp internalHandler(Payload<ProductUpdateForm> request) {
        return productService.updateProduct(request.data());
    }

    @Override
    protected void inject() {
        DependenciesResolver resolver = getContainerResolver().getDependenciesResolver();
        if (productService == null) {
            productService = resolver.resolveDependencies(ProductService.class);
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
        return RequestMethod.PUT;
    }
}
