package com.bachlinh.order.web.service.common;

import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.web.dto.resp.EmailInfoResp;

public interface EmailService {
    EmailInfoResp getEmailOfCustomer(String id, Customer owner);
}
