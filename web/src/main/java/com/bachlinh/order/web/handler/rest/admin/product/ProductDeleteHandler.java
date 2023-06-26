package com.bachlinh.order.web.handler.rest.admin.product;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.exception.http.BadVariableException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.form.DeleteProductForm;
import com.bachlinh.order.web.service.common.ProductService;

import java.util.HashMap;
import java.util.Map;

@ActiveReflection
@RouteProvider
@NoArgsConstructor(onConstructor_ = @ActiveReflection)
public class ProductDeleteHandler extends AbstractController<ResponseEntity<Map<String, Object>>, DeleteProductForm> {
    private String url;
    private ProductService productService;

    @Override
    @ActiveReflection
    protected ResponseEntity<Map<String, Object>> internalHandler(Payload<DeleteProductForm> request) {
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

    private ResponseEntity<Map<String, Object>> deleteProduct(String productId) {
        var result = productService.deleteProduct(productId);
        Map<String, Object> resp = new HashMap<>();
        if (!result) {
            throw new BadVariableException("Can not delete product has id [" + productId + "]");
        }
        resp.put("status", HttpStatus.ACCEPTED.value());
        resp.put("messages", new String[]{"Delete product has id [" + productId + "] success"});
        return ResponseEntity.accepted().body(resp);
    }
}
