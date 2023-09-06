package com.bachlinh.order.web.handler.rest.admin.product;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.EnableCsrf;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.Permit;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.exception.http.BadVariableException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.form.admin.product.ProductDeleteForm;
import com.bachlinh.order.web.service.common.ProductService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

@ActiveReflection
@RouteProvider(name = "productDeleteHandler")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Permit(roles = Role.ADMIN)
@EnableCsrf
public class ProductDeleteHandler extends AbstractController<ResponseEntity<Map<String, Object>>, ProductDeleteForm> {
    private String url;
    private ProductService productService;

    @Override
    public AbstractController<ResponseEntity<Map<String, Object>>, ProductDeleteForm> newInstance() {
        return new ProductDeleteHandler();
    }

    @Override
    @ActiveReflection
    protected ResponseEntity<Map<String, Object>> internalHandler(Payload<ProductDeleteForm> request) {
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
