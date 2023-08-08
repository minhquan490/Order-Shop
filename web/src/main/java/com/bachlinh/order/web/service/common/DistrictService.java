package com.bachlinh.order.web.service.common;

import com.bachlinh.order.web.dto.resp.DistrictResp;

import java.util.Collection;

public interface DistrictService {
    Collection<DistrictResp> getDistrictByProvince(String provinceId);
}
