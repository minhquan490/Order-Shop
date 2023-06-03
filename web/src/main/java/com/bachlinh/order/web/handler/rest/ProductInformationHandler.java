package com.bachlinh.order.web.handler.rest;

import org.springframework.http.ResponseEntity;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.exception.http.ResourceNotFoundException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.service.Form;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.form.ProductForm;
import com.bachlinh.order.web.dto.resp.ProductResp;
import com.bachlinh.order.web.service.common.ProductService;

@ActiveReflection
@RouteProvider
public class ProductInformationHandler extends AbstractController<ResponseEntity<ProductResp>, Object> {
    private String productInfoUrl;
    private ProductService productService;

    @ActiveReflection
    public ProductInformationHandler() {
    }

    @Override
    protected ResponseEntity<ProductResp> internalHandler(Payload<Object> request) {
        String productId = getNativeRequest().getUrlQueryParam().getFirst("id");
        if (productId == null || productId.isBlank()) {
            throw new ResourceNotFoundException("Specific url is not found", productInfoUrl);
        }
        return productInformation(productId);
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
        if (productInfoUrl == null) {
            productInfoUrl = getEnvironment().getProperty("shop.url.content.product.info");
        }
        return productInfoUrl;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }

    private ResponseEntity<ProductResp> productInformation(String productId) {
        var form = new ProductForm();
        form.setId(productId);
        ProductResp result = productService.getOne(Form.wrap(form)).get();
        return ResponseEntity.ok(result);
    }
}
