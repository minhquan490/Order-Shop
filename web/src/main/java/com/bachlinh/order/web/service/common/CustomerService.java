package com.bachlinh.order.web.service.common;

import com.bachlinh.order.http.NativeResponse;
import com.bachlinh.order.web.dto.form.admin.customer.CustomerCreateForm;
import com.bachlinh.order.web.dto.form.admin.customer.CustomerDeleteForm;
import com.bachlinh.order.web.dto.form.admin.customer.CustomerUpdateInfoForm;
import com.bachlinh.order.web.dto.form.customer.CustomerUpdateForm;
import com.bachlinh.order.web.dto.resp.CustomerBasicInformationResp;
import com.bachlinh.order.web.dto.resp.CustomerInfoResp;
import com.bachlinh.order.web.dto.resp.CustomerResp;
import com.bachlinh.order.web.dto.resp.MyInfoResp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService {

    MyInfoResp getMyInfo(String customerId);

    Page<CustomerResp> getFullInformationOfCustomer(Pageable pageable);

    CustomerResp saveCustomer(CustomerCreateForm customerCreateForm);

    CustomerResp updateCustomer(CustomerUpdateForm customerUpdateForm);

    CustomerInfoResp updateCustomerFromAdminScreen(CustomerUpdateInfoForm customerUpdateInfoForm);

    CustomerResp deleteCustomer(CustomerDeleteForm customerDeleteForm);

    CustomerInfoResp getCustomerInfo(String customerId);

    CustomerBasicInformationResp basicCustomerInfo(String accessToken, NativeResponse<?> response);
}
