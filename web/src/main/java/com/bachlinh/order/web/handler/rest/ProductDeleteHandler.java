package com.bachlinh.order.web.handler.rest;

import org.springframework.http.ResponseEntity;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.service.Form;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.form.DeleteProductForm;
import com.bachlinh.order.web.dto.form.ProductForm;
import com.bachlinh.order.web.service.common.ProductService;

import java.util.HashMap;
import java.util.Map;

@ActiveReflection
@RouteProvider
public class ProductDeleteHandler extends AbstractController<ResponseEntity<Map<String, String>>, DeleteProductForm> {
    private String url;
    private ProductService productService;

    @ActiveReflection
    public ProductDeleteHandler() {
    }

    @Override
    protected ResponseEntity<Map<String, String>> internalHandler(Payload<DeleteProductForm> request) {
        String productId = request.data().productId();
        return deleteProduct(productId);
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
            url = getEnvironment().getProperty("shop.url.admin.product.delete");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.DELETE;
    }

    private ResponseEntity<Map<String, String>> deleteProduct(String productId) {
        var form = new ProductForm();
        form.setId(productId);
        var result = productService.delete(Form.wrap(form)).get();
        Map<String, String> resp = new HashMap<>();
        if (result.isSuccess()) {
            resp.put("message", "Delete success");
        } else {
            resp.put("message", "Delete failure");
        }
        return ResponseEntity.accepted().body(resp);
    }
}
