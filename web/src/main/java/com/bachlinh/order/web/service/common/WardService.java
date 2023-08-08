package com.bachlinh.order.web.service.common;

import com.bachlinh.order.web.dto.resp.WardResp;

import java.util.Collection;

public interface WardService {
    Collection<WardResp> getWardsByDistrict(String districtId);
}
