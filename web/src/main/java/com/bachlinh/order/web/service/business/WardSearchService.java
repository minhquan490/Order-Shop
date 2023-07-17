package com.bachlinh.order.web.service.business;

import com.bachlinh.order.web.dto.form.common.WardSearchForm;
import com.bachlinh.order.web.dto.resp.WardResp;

import java.util.Collection;

public interface WardSearchService {
    Collection<WardResp> search(WardSearchForm form);
}
