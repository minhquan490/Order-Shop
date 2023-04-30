package com.bachlinh.order.web.service.common;

import com.bachlinh.order.service.BaseService;
import com.bachlinh.order.web.dto.form.CrudCustomerForm;
import com.bachlinh.order.web.dto.resp.CustomerInformationResp;
import com.bachlinh.order.web.dto.resp.CustomerResp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService extends BaseService<CustomerInformationResp, CrudCustomerForm> {

    CustomerInformationResp getCustomerInformation(String customerId);

    Page<CustomerResp> getFullInformationOfCustomer(Pageable pageable);
}
