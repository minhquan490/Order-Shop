package com.bachlinh.order.web.handler.rest.customer.product;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.exception.http.BadVariableException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.ProductResp;
import com.bachlinh.order.web.service.common.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Collection;
import java.util.Optional;

@ActiveReflection
@RouteProvider(name = "productListHandler")
public class ProductListHandler extends AbstractController<Collection<ProductResp>, Object> {

    private ProductService productService;
    private String productListUrl;

    private ProductListHandler() {
    }

    @Override
    public AbstractController<Collection<ProductResp>, Object> newInstance() {
        return new ProductListHandler();
    }

    @Override
    @ActiveReflection
    protected Collection<ProductResp> internalHandler(Payload<Object> request) {
        String page = Optional.ofNullable(getNativeRequest().getUrlQueryParam().getFirst("page")).orElse("1");
        String size = Optional.ofNullable(getNativeRequest().getUrlQueryParam().getFirst("size")).orElse("100");
        return productList(page, size).toList();
    }

    @Override
    protected void inject() {
        if (productService == null) {
            productService = resolveService(ProductService.class);
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
