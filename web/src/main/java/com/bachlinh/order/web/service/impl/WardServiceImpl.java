package com.bachlinh.order.web.service.impl;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.ServiceComponent;
import com.bachlinh.order.dto.DtoMapper;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.Ward;
import com.bachlinh.order.repository.WardRepository;
import com.bachlinh.order.web.dto.form.common.WardSearchForm;
import com.bachlinh.order.web.dto.resp.WardResp;
import com.bachlinh.order.web.service.business.WardSearchService;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

@ServiceComponent
@ActiveReflection
@RequiredArgsConstructor(onConstructor = @__(@ActiveReflection))
public class WardServiceImpl implements WardSearchService {
    private final EntityFactory entityFactory;
    private final WardRepository wardRepository;
    private final DtoMapper dtoMapper;

    @Override
    public Collection<WardResp> search(WardSearchForm form) {
        var context = entityFactory.getEntityContext(Ward.class);
        var ids = context.search(form.getQuery());
        var wards = wardRepository.getWards(ids.stream().map(Integer::parseInt).toList());
        return dtoMapper.map(wards, WardResp.class);
    }
}
