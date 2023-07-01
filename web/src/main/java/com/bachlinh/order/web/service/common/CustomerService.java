package com.bachlinh.order.web.service.common;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.bachlinh.order.web.dto.form.admin.customer.CustomerCreateForm;
import com.bachlinh.order.web.dto.form.admin.customer.CustomerDeleteForm;
import com.bachlinh.order.web.dto.form.admin.customer.CustomerUpdateForm;
import com.bachlinh.order.web.dto.resp.CustomerInformationResp;
import com.bachlinh.order.web.dto.resp.CustomerResp;
import com.bachlinh.order.web.dto.resp.TableCustomerInfoResp;

import java.util.Collection;

public interface CustomerService {

    CustomerInformationResp getCustomerInformation(String customerId);

    Page<CustomerResp> getFullInformationOfCustomer(Pageable pageable);

    Collection<TableCustomerInfoResp> getCustomerDataTable();

    CustomerInformationResp saveCustomer(CustomerCreateForm customerCreateForm);

    CustomerInformationResp updateCustomer(CustomerUpdateForm customerUpdateForm);

    CustomerInformationResp deleteCustomer(CustomerDeleteForm customerDeleteForm);
}
