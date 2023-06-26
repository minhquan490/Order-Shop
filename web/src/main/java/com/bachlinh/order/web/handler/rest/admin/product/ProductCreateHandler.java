package com.bachlinh.order.web.handler.rest.admin.product;

import lombok.NoArgsConstructor;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.form.admin.ProductCreateForm;
import com.bachlinh.order.web.dto.resp.ProductResp;
import com.bachlinh.order.web.service.common.ProductService;

@ActiveReflection
@RouteProvider
@NoArgsConstructor(onConstructor_ = @ActiveReflection)
public class ProductCreateHandler extends AbstractController<ProductResp, ProductCreateForm> {
    private String url;
    private ProductService productService;

    @Override
    @ActiveReflection
    protected ProductResp internalHandler(Payload<ProductCreateForm> request) {
        return createProduct(request.data());
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
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.admin.product.create");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.POST;
    }

    private ProductResp createProduct(ProductCreateForm form) {
        return productService.createProduct(form);
    }
}
