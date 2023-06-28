package com.bachlinh.order.web.handler.rest.customer.cart;

import lombok.NoArgsConstructor;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.CartForm;
import com.bachlinh.order.web.dto.resp.CartResp;
import com.bachlinh.order.web.service.common.CartService;

@ActiveReflection
@RouteProvider
@NoArgsConstructor(onConstructor = @__({@ActiveReflection}))
public class CartProductRemoveHandler extends AbstractController<CartResp, CartForm> {

    private CartService cartService;
    private String url;

    @Override
    @ActiveReflection
    protected CartResp internalHandler(Payload<CartForm> request) {
        return cartService.removeProductFromCart(request.data());
    }

    @Override
    protected void inject() {
        var resolver = getContainerResolver().getDependenciesResolver();
        if (cartService == null) {
            cartService = resolver.resolveDependencies(CartService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.customer.cart.remove");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.PUT;
    }
}
