package com.bachlinh.order.web.handler.rest.customer.cart;

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
import com.bachlinh.order.web.dto.form.customer.CartForm;
import com.bachlinh.order.web.dto.resp.CartResp;
import com.bachlinh.order.web.service.common.CartService;

@RouteProvider(name = "cartUpdateHandler")
@ActiveReflection
@Permit(roles = {Role.CUSTOMER, Role.ADMIN})
@EnableCsrf
public class CartUpdateHandler extends AbstractController<CartResp, CartForm> {
    private CartService cartService;
    private String url;

    private CartUpdateHandler() {
    }

    @Override
    public AbstractController<CartResp, CartForm> newInstance() {
        return new CartUpdateHandler();
    }

    @Override
    @ActiveReflection
    @Transactional(isolation = Isolation.READ_COMMITTED, timeOut = 10)
    protected CartResp internalHandler(Payload<CartForm> request) {
        return cartService.updateCart(request.data());
    }

    @Override
    protected void inject() {
        if (cartService == null) {
            cartService = resolveService(CartService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.customer.cart.update");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.PATCH;
    }
}
