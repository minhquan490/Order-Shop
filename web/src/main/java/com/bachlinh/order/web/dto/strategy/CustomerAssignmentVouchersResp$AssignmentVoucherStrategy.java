package com.bachlinh.order.web.dto.strategy;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.dto.strategy.AbstractDtoStrategy;
import com.bachlinh.order.dto.strategy.DtoStrategy;
import com.bachlinh.order.entity.model.Voucher;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.utils.DateTimeUtils;
import com.bachlinh.order.web.dto.resp.CustomerAssignmentVouchersResp;

@ActiveReflection
public class CustomerAssignmentVouchersResp$AssignmentVoucherStrategy extends AbstractDtoStrategy<CustomerAssignmentVouchersResp.AssignmentVoucher, Voucher> {

    private CustomerAssignmentVouchersResp$AssignmentVoucherStrategy(DependenciesResolver dependenciesResolver, Environment environment) {
        super(dependenciesResolver, environment);
    }

    @Override
    protected void beforeConvert(Voucher source, Class<CustomerAssignmentVouchersResp.AssignmentVoucher> type) {
        // Do nothing
    }

    @Override
    protected CustomerAssignmentVouchersResp.AssignmentVoucher doConvert(Voucher source, Class<CustomerAssignmentVouchersResp.AssignmentVoucher> type) {
        CustomerAssignmentVouchersResp.AssignmentVoucher result = new CustomerAssignmentVouchersResp.AssignmentVoucher();
        result.setId(source.getId());
        result.setName(source.getName());
        result.setTimeStart(DateTimeUtils.convertOutputDateTime(source.getTimeStart()));
        result.setTimeExpired(DateTimeUtils.convertOutputDateTime(source.getTimeExpired()));
        result.setDiscountPercent(String.valueOf(source.getDiscountPercent()));
        return result;
    }

    @Override
    protected void afterConvert(Voucher source, Class<CustomerAssignmentVouchersResp.AssignmentVoucher> type) {
        // Do nothing
    }

    @Override
    protected DtoStrategy<CustomerAssignmentVouchersResp.AssignmentVoucher, Voucher> createNew(DependenciesResolver dependenciesResolver, Environment environment) {
        return new CustomerAssignmentVouchersResp$AssignmentVoucherStrategy(dependenciesResolver, environment);
    }

    @Override
    public Class<CustomerAssignmentVouchersResp.AssignmentVoucher> getTargetType() {
        return CustomerAssignmentVouchersResp.AssignmentVoucher.class;
    }
}
