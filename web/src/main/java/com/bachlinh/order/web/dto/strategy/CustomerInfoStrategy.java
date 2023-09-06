package com.bachlinh.order.web.dto.strategy;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.dto.strategy.AbstractDtoStrategy;
import com.bachlinh.order.dto.strategy.DtoStrategy;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.resp.CustomerInfoResp;

@ActiveReflection
public class CustomerInfoStrategy extends AbstractDtoStrategy<CustomerInfoResp, Customer> {

    @ActiveReflection
    private CustomerInfoStrategy(DependenciesResolver dependenciesResolver, Environment environment) {
        super(dependenciesResolver, environment);
    }

    @Override
    protected void beforeConvert(Customer source, Class<CustomerInfoResp> type) {
        // Do nothing
    }

    @Override
    protected CustomerInfoResp doConvert(Customer source, Class<CustomerInfoResp> type) {
        var resp = new CustomerInfoResp();
        resp.setId(source.getId());
        resp.setFirstName(source.getFirstName());
        resp.setLastName(source.getLastName());
        resp.setPhoneNumber(source.getPhoneNumber());
        resp.setEmail(source.getEmail());
        resp.setGender(source.getGender().toLowerCase());
        resp.setRole(source.getRole().toLowerCase());
        resp.setUsername(source.getUsername());
        resp.setAddress(source.getAddressString());
        resp.setActivated(source.isActivated());
        resp.setAccountNonExpired(source.isAccountNonExpired());
        resp.setAccountNonLocked(source.isAccountNonLocked());
        resp.setCredentialsNonExpired(source.isCredentialsNonExpired());
        resp.setOrderPoint(source.getOrderPoint());
        resp.setEnabled(source.isEnabled());
        resp.setPicture(source.getPicture());
        resp.setHistories(source.getHistories()
                .stream()
                .limit(3)
                .map(customerAccessHistory -> {
                    var historyResp = new CustomerInfoResp.History();
                    historyResp.setPathRequest(customerAccessHistory.getPathRequest());
                    historyResp.setRequestTime(customerAccessHistory.getRequestTime().toString());
                    historyResp.setRequestType(customerAccessHistory.getRequestType());
                    return historyResp;
                })
                .toList()
                .toArray(new CustomerInfoResp.History[0])
        );
        resp.setVouchers(source.getAssignedVouchers()
                .stream()
                .limit(3)
                .map(voucher -> {
                    var voucherResp = new CustomerInfoResp.Voucher();
                    voucherResp.setId(voucher.getId());
                    voucherResp.setName(voucher.getName());
                    return voucherResp;
                })
                .toList()
                .toArray(new CustomerInfoResp.Voucher[0])
        );
        resp.setOrders(source.getOrders()
                .stream()
                .limit(3)
                .map(order -> {
                    var orderResp = new CustomerInfoResp.Order();
                    orderResp.setId(order.getId());
                    orderResp.setTimeOrder(order.getTimeOrder().toString());
                    orderResp.setStatus(order.getOrderStatus().getStatus());
                    orderResp.setTransactionCode(order.getBankTransactionCode());
                    return orderResp;
                })
                .toList()
                .toArray(new CustomerInfoResp.Order[0])
        );
        return resp;
    }

    @Override
    protected void afterConvert(Customer source, Class<CustomerInfoResp> type) {
        // Do nothing
    }

    @Override
    protected DtoStrategy<CustomerInfoResp, Customer> createNew(DependenciesResolver dependenciesResolver, Environment environment) {
        return new CustomerInfoStrategy(dependenciesResolver, environment);
    }

    @Override
    public Class<CustomerInfoResp> getTargetType() {
        return CustomerInfoResp.class;
    }
}
