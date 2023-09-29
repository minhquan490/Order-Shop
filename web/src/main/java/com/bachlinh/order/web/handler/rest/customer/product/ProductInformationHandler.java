package com.bachlinh.order.web.handler.rest.customer.product;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.exception.http.ResourceNotFoundException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.ProductResp;
import com.bachlinh.order.web.service.common.ProductService;
import org.springframework.util.StringUtils;

@ActiveReflection
@RouteProvider(name = "productInformationHandler")
public class ProductInformationHandler extends AbstractController<ProductResp, Void> {
    private String productInfoUrl;
    private ProductService productService;

    private ProductInformationHandler() {
    }

    @Override
    public AbstractController<ProductResp, Void> newInstance() {
        return new ProductInformationHandler();
    }

    @Override
    @ActiveReflection
    protected ProductResp internalHandler(Payload<Void> request) {
        String productId = getNativeRequest().getUrlQueryParam().getFirst("id");
        if (!StringUtils.hasText(productId)) {
            throw new ResourceNotFoundException("Specific url is not found", productInfoUrl);
        }
        return productInformation(productId);
    }

    @Override
    protected void inject() {
        if (productService == null) {
            productService = resolveService(ProductService.class);
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

    private ProductResp productInformation(String productId) {
        return productService.getProductById(productId);
    }
}
