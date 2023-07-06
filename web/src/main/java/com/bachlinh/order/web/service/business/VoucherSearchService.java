package com.bachlinh.order.web.service.business;

import com.bachlinh.order.web.dto.form.admin.voucher.VoucherSearchForm;
import com.bachlinh.order.web.dto.resp.VoucherResp;

import java.util.Collection;

public interface VoucherSearchService {
    Collection<VoucherResp> search(VoucherSearchForm form);
}
