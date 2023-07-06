package com.bachlinh.order.web.service.business;

import com.bachlinh.order.web.dto.form.common.DistrictSearchForm;
import com.bachlinh.order.web.dto.resp.DistrictResp;

import java.util.Collection;

public interface DistrictSearchService {
    Collection<DistrictResp> search(DistrictSearchForm form);
}
