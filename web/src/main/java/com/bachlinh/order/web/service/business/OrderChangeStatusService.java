package com.bachlinh.order.web.service.business;

import com.bachlinh.order.web.dto.form.admin.order.OrderChangeStatusForm;

public interface OrderChangeStatusService {
    void updateOrderStatus(OrderChangeStatusForm form);
}
