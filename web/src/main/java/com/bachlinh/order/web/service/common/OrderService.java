package com.bachlinh.order.web.service.common;

import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.web.dto.form.admin.order.OrderProductForm;
import com.bachlinh.order.web.dto.form.customer.OrderCreateForm;
import com.bachlinh.order.web.dto.resp.OrderInfoResp;
import com.bachlinh.order.web.dto.resp.OrderOfCustomerResp;
import com.bachlinh.order.web.dto.resp.OrderResp;

import java.util.Collection;

public interface OrderService {

    OrderResp saveOrder(OrderCreateForm form);

    OrderResp updateOrder(OrderProductForm form);

    OrderResp getOrderById(String orderId);

    OrderInfoResp getOrderInfoById(String orderId);

    boolean deleteOrder(String orderId);

    boolean orderIsExist(String orderId);

    Collection<OrderResp> getAllOrder();

    OrderOfCustomerResp getOrdersOfCustomer(NativeRequest<?> request);
}
