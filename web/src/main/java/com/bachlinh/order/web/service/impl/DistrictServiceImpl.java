package com.bachlinh.order.web.service.impl;

import com.bachlinh.order.core.annotation.ServiceComponent;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.dto.DtoMapper;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.District;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.handler.service.AbstractService;
import com.bachlinh.order.handler.service.ServiceBase;
import com.bachlinh.order.repository.DistrictRepository;
import com.bachlinh.order.web.dto.form.common.DistrictSearchForm;
import com.bachlinh.order.web.dto.resp.DistrictResp;
import com.bachlinh.order.web.service.business.DistrictSearchService;
import com.bachlinh.order.web.service.common.DistrictService;

import java.util.Collection;

@ServiceComponent
public class DistrictServiceImpl extends AbstractService implements DistrictSearchService, DistrictService {
    private final DistrictRepository districtRepository;
    private final EntityFactory entityFactory;
    private final DtoMapper dtoMapper;

    private DistrictServiceImpl(DependenciesResolver resolver, Environment environment) {
        super(resolver, environment);
        this.districtRepository = resolveRepository(DistrictRepository.class);
        this.entityFactory = resolver.resolveDependencies(EntityFactory.class);
        this.dtoMapper = resolver.resolveDependencies(DtoMapper.class);
    }

    @Override
    public Collection<DistrictResp> search(DistrictSearchForm form) {
        var context = entityFactory.getEntityContext(District.class);
        Collection<String> ids = context.search(form.getQuery());
        var districts = districtRepository.getDistricts(ids);
        return dtoMapper.map(districts, DistrictResp.class);
    }

    @Override
    public Collection<DistrictResp> getDistrictByProvince(String provinceId) {
        return dtoMapper.map(districtRepository.getDistrictsByProvince(provinceId), DistrictResp.class);
    }

    @Override
    public ServiceBase getInstance(DependenciesResolver resolver, Environment environment) {
        return new DistrictServiceImpl(resolver, environment);
    }

    @Override
    public Class<?>[] getServiceTypes() {
        return new Class[]{DistrictSearchService.class, DistrictService.class};
    }
}
