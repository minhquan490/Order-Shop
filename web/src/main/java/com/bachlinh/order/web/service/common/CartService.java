package com.bachlinh.order.web.service.common;

import com.bachlinh.order.web.dto.form.CartForm;
import com.bachlinh.order.web.dto.resp.CartResp;

public interface CartService {
    CartResp updateCart(CartForm form);

    CartResp removeProductFromCart(CartForm form);
}
