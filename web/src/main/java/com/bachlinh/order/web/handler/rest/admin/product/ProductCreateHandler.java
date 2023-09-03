package com.bachlinh.order.web.handler.rest.admin.product;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.EnableCsrf;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.Permit;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.form.admin.product.ProductCreateForm;
import com.bachlinh.order.web.dto.resp.ProductResp;
import com.bachlinh.order.web.service.common.ProductService;
import lombok.NoArgsConstructor;

@ActiveReflection
@RouteProvider(name = "productCreateHandler")
@NoArgsConstructor(onConstructor = @__({@ActiveReflection}))
@Permit(roles = Role.ADMIN)
@EnableCsrf
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
