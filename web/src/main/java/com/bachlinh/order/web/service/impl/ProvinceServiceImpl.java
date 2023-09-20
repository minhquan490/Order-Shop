package com.bachlinh.order.web.service.impl;

import com.bachlinh.order.annotation.ServiceComponent;
import com.bachlinh.order.dto.DtoMapper;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.Province;
import com.bachlinh.order.repository.ProvinceRepository;
import com.bachlinh.order.web.dto.form.common.ProvinceSearchForm;
import com.bachlinh.order.web.dto.resp.ProvinceResp;
import com.bachlinh.order.web.service.business.ProvinceSearchService;
import com.bachlinh.order.web.service.common.ProvinceService;

import java.util.Collection;

@ServiceComponent
public class ProvinceServiceImpl implements ProvinceSearchService, ProvinceService {
    private final ProvinceRepository provinceRepository;
    private final EntityFactory entityFactory;
    private final DtoMapper dtoMapper;

    public ProvinceServiceImpl(ProvinceRepository provinceRepository, EntityFactory entityFactory, DtoMapper dtoMapper) {
        this.provinceRepository = provinceRepository;
        this.entityFactory = entityFactory;
        this.dtoMapper = dtoMapper;
    }

    @Override
    public Collection<ProvinceResp> search(ProvinceSearchForm form) {
        var context = entityFactory.getEntityContext(Province.class);
        var ids = context.search(form.getQuery());
        var result = provinceRepository.getProvincesById(ids);
        return dtoMapper.map(result, ProvinceResp.class);
    }

    @Override
    public Collection<ProvinceResp> getAllProvince() {
        var provinces = provinceRepository.getAllProvinces();
        return dtoMapper.map(provinces, ProvinceResp.class);
    }
}
