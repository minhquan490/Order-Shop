package com.bachlinh.order.web.handler.rest.admin.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.exception.http.BadVariableException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.resp.ProductResp;
import com.bachlinh.order.web.service.common.ProductService;

import java.util.Optional;

@ActiveReflection
@RouteProvider
public class ProductListHandler extends AbstractController<Page<ProductResp>, Object> {

    private ProductService productService;
    private String productListUrl;

    @ActiveReflection
    public ProductListHandler() {
        // Default constructor
    }

    @Override
    protected Page<ProductResp> internalHandler(Payload<Object> request) {
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

    private Page<ProductResp> productList(String page, String size) {
        int p;
        int s;
        try {
            p = Integer.parseInt(page);
            s = Integer.parseInt(size);
        } catch (NumberFormatException e) {
            throw new BadVariableException("Page number and page size must be int");
        }

        PageRequest pageRequest = PageRequest.of(p, s);
        return productService.productList(pageRequest);
    }
}
