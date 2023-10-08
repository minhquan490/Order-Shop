package com.bachlinh.order.web.handler.rest.admin.product;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.EnableCsrf;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.annotation.Transactional;
import com.bachlinh.order.core.enums.Isolation;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.core.annotation.Permit;
import com.bachlinh.order.core.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.admin.product.ProductCreateForm;
import com.bachlinh.order.web.dto.resp.ProductResp;
import com.bachlinh.order.web.service.common.ProductService;

@ActiveReflection
@RouteProvider(name = "productCreateHandler")
@Permit(roles = Role.ADMIN)
@EnableCsrf
public class ProductCreateHandler extends AbstractController<ProductResp, ProductCreateForm> {
    private String url;
    private ProductService productService;

    private ProductCreateHandler() {
    }

    @Override
    public AbstractController<ProductResp, ProductCreateForm> newInstance() {
        return new ProductCreateHandler();
    }

    @Override
    @ActiveReflection
    @Transactional(isolation = Isolation.READ_COMMITTED, timeOut = 10)
    protected ProductResp internalHandler(Payload<ProductCreateForm> request) {
        return createProduct(request.data());
    }

    @Override
    protected void inject() {
        if (productService == null) {
            productService = resolveService(ProductService.class);
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

    private ProductResp createProduct(ProductCreateForm form) {
        return productService.createProduct(form);
    }
}
