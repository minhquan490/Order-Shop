package com.bachlinh.order.web.service.impl;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.ServiceComponent;
import com.bachlinh.order.dto.DtoMapper;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.entity.model.Ward;
import com.bachlinh.order.exception.http.BadVariableException;
import com.bachlinh.order.repository.DistrictRepository;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.repository.WardRepository;
import com.bachlinh.order.web.dto.form.common.WardSearchForm;
import com.bachlinh.order.web.dto.resp.WardResp;
import com.bachlinh.order.web.service.business.WardSearchService;
import com.bachlinh.order.web.service.common.WardService;

import java.text.MessageFormat;
import java.util.Collection;

@ServiceComponent
@ActiveReflection
public class WardServiceImpl implements WardSearchService, WardService {
    private static final String NOT_EXISTED_MESSAGE_ID = "MSG-000017";

    private final EntityFactory entityFactory;
    private final WardRepository wardRepository;
    private final DistrictRepository districtRepository;
    private final DtoMapper dtoMapper;
    private final MessageSettingRepository messageSettingRepository;

    @ActiveReflection
    public WardServiceImpl(EntityFactory entityFactory, WardRepository wardRepository, DistrictRepository districtRepository, DtoMapper dtoMapper, MessageSettingRepository messageSettingRepository) {
        this.entityFactory = entityFactory;
        this.wardRepository = wardRepository;
        this.districtRepository = districtRepository;
        this.dtoMapper = dtoMapper;
        this.messageSettingRepository = messageSettingRepository;
    }

    @Override
    public Collection<WardResp> search(WardSearchForm form) {
        var context = entityFactory.getEntityContext(Ward.class);
        var ids = context.search(form.getQuery());
        var wards = wardRepository.getWards(ids.stream().map(Integer::parseInt).toList());
        return dtoMapper.map(wards, WardResp.class);
    }

    @Override
    public Collection<WardResp> getWardsByDistrict(String districtId) {
        var district = districtRepository.getDistrictById(districtId);
        if (district == null) {
            MessageSetting messageSetting = messageSettingRepository.getMessageById(NOT_EXISTED_MESSAGE_ID);
            String errorContent = MessageFormat.format(messageSetting.getValue(), "ID of district");
            throw new BadVariableException(errorContent);
        }
        var wards = wardRepository.getWardsByDistrict(district);
        return dtoMapper.map(wards, WardResp.class);
    }
}
