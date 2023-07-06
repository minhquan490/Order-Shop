package com.bachlinh.order.web.service.common;

import com.bachlinh.order.web.dto.form.admin.voucher.VoucherCreateForm;
import com.bachlinh.order.web.dto.form.admin.voucher.VoucherDeleteForm;
import com.bachlinh.order.web.dto.form.admin.voucher.VoucherUpdateForm;
import com.bachlinh.order.web.dto.resp.VoucherResp;

import java.util.Collection;
import java.util.Map;

public interface VoucherService {
    VoucherResp createVoucher(VoucherCreateForm form);

    VoucherResp updateVoucher(VoucherUpdateForm form);

    Map<String, Object> deleteVoucher(VoucherDeleteForm form);

    Collection<VoucherResp> getVouchers();

    Collection<VoucherResp> getVoucherSByStatus(boolean status);
}
