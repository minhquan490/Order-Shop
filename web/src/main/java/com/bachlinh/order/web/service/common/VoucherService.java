package com.bachlinh.order.web.service.common;

import com.bachlinh.order.http.NativeRequest;
import com.bachlinh.order.web.dto.form.admin.voucher.VoucherCreateForm;
import com.bachlinh.order.web.dto.form.admin.voucher.VoucherDeleteForm;
import com.bachlinh.order.web.dto.form.admin.voucher.VoucherUpdateForm;
import com.bachlinh.order.web.dto.resp.CustomerAssignmentVouchersResp;
import com.bachlinh.order.web.dto.resp.VoucherResp;

import java.util.Collection;

public interface VoucherService {
    VoucherResp createVoucher(VoucherCreateForm form);

    VoucherResp updateVoucher(VoucherUpdateForm form);

    void deleteVoucher(VoucherDeleteForm form);

    Collection<VoucherResp> getVouchers();

    Collection<VoucherResp> getVouchersByStatus(boolean status);

    CustomerAssignmentVouchersResp getAssignVouchers(NativeRequest<?> request);
}
