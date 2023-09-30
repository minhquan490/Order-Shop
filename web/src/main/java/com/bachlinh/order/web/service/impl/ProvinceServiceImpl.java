package com.bachlinh.order.web.service.impl;

import com.bachlinh.order.core.annotation.ServiceComponent;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.dto.DtoMapper;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.Province;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.handler.service.AbstractService;
import com.bachlinh.order.handler.service.ServiceBase;
import com.bachlinh.order.repository.ProvinceRepository;
import com.bachlinh.order.web.dto.form.common.ProvinceSearchForm;
import com.bachlinh.order.web.dto.resp.ProvinceResp;
import com.bachlinh.order.web.service.business.ProvinceSearchService;
import com.bachlinh.order.web.service.common.ProvinceService;

import java.util.Collection;

@ServiceComponent
public class ProvinceServiceImpl extends AbstractService implements ProvinceSearchService, ProvinceService {
    private final ProvinceRepository provinceRepository;
    private final EntityFactory entityFactory;
    private final DtoMapper dtoMapper;

    private ProvinceServiceImpl(DependenciesResolver resolver, Environment environment) {
        super(resolver, environment);
        this.provinceRepository = resolveRepository(ProvinceRepository.class);
        this.entityFactory = resolver.resolveDependencies(EntityFactory.class);
        this.dtoMapper = resolver.resolveDependencies(DtoMapper.class);
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

    @Override
    public ServiceBase getInstance(DependenciesResolver resolver, Environment environment) {
        return new ProvinceServiceImpl(resolver, environment);
    }

    @Override
    public Class<?>[] getServiceTypes() {
        return new Class[]{ProvinceSearchService.class, ProvinceService.class};
    }
}
