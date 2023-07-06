package com.bachlinh.order.web.service.business;

import com.bachlinh.order.web.dto.form.common.ProvinceSearchForm;
import com.bachlinh.order.web.dto.resp.ProvinceResp;

import java.util.Collection;

public interface ProvinceSearchService {
    Collection<ProvinceResp> search(ProvinceSearchForm form);
}
