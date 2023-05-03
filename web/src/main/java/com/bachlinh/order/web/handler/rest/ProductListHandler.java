package com.bachlinh.order.web.handler.rest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.exception.http.BadVariableException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.resp.ProductResp;
import com.bachlinh.order.web.service.common.ProductService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ActiveReflection
@RouteProvider
public class ProductListHandler extends AbstractController<ResponseEntity<Map<String, Object>>, Object> {

    private ProductService productService;
    private String productListUrl;

    @ActiveReflection
    public ProductListHandler() {
        // Default constructor
    }

    @Override
    protected ResponseEntity<Map<String, Object>> internalHandler(Payload<Object> request) {
        String page = Optional.ofNullable(getNativeRequest().getUrlQueryParam().getFirst("page")).orElse("1");
        String size = Optional.ofNullable(getNativeRequest().getUrlQueryParam().getFirst("size")).orElse("100");
        return productList(page, size);
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
        if (productListUrl == null) {
            productListUrl = getEnvironment().getProperty("shop.url.content.product.list");
        }
        return productListUrl;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }

    private ResponseEntity<Map<String, Object>> productList(String page, String size) {
        int p;
        int s;
        try {
            p = Integer.parseInt(page);
            s = Integer.parseInt(size);
        } catch (NumberFormatException e) {
            throw new BadVariableException("Page number and page size must be int");
        }

        PageRequest pageRequest = PageRequest.of(p, s);
        Map<String, Object> resp = new HashMap<>();
        Page<ProductResp> results = productService.productList(pageRequest);
        resp.put("products", results);
        return ResponseEntity.ok(resp);
    }
}
