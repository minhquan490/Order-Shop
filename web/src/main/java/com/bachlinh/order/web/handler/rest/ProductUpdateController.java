package com.bachlinh.order.web.handler.rest;

import org.springframework.http.ResponseEntity;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.service.Form;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.form.ProductForm;
import com.bachlinh.order.web.dto.resp.ProductResp;
import com.bachlinh.order.web.service.common.ProductService;

@RouteProvider
@ActiveReflection
public class ProductUpdateController extends AbstractController<ResponseEntity<ProductResp>, ProductForm> {
    private String productUpdateUrl;
    private ProductService productService;

    @ActiveReflection
    public ProductUpdateController() {
    }

    @Override
    protected ResponseEntity<ProductResp> internalHandler(Payload<ProductForm> request) {
        ProductResp resp = productService.update(Form.wrap(request.data())).get();
        return ResponseEntity.ok(resp);
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
