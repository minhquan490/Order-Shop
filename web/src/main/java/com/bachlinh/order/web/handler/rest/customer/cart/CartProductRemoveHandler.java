package com.bachlinh.order.web.handler.rest.customer.cart;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.Permit;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.customer.CartDetailRemoveForm;
import com.bachlinh.order.web.dto.resp.CartResp;
import com.bachlinh.order.web.service.common.CartService;
import lombok.NoArgsConstructor;

@ActiveReflection
@RouteProvider(name = "cartProductRemoveHandler")
@NoArgsConstructor(onConstructor = @__({@ActiveReflection}))
@Permit(roles = Role.CUSTOMER)
public class CartProductRemoveHandler extends AbstractController<CartResp, CartDetailRemoveForm> {

    private CartService cartService;
    private String url;

    @Override
    @ActiveReflection
    protected CartResp internalHandler(Payload<CartDetailRemoveForm> request) {
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
        return RequestMethod.DELETE;
    }
}
