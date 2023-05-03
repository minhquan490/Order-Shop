package com.bachlinh.order.web.handler.rest;

import org.springframework.http.HttpStatus;
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

@ActiveReflection
@RouteProvider
public class ProductCreateHandler extends AbstractController<ResponseEntity<ProductResp>, ProductForm> {
    private String url;
    private ProductService productService;

    @ActiveReflection
    public ProductCreateHandler() {
    }

    @Override
    protected ResponseEntity<ProductResp> internalHandler(Payload<ProductForm> request) {
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

    private ResponseEntity<ProductResp> createProduct(ProductForm form) {
        ProductResp resp = productService.save(Form.wrap(form)).get();
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }
}
