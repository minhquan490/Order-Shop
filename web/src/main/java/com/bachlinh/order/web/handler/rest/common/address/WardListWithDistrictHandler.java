package com.bachlinh.order.web.handler.rest.common.address;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.exception.http.BadVariableException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.utils.ValidateUtils;
import com.bachlinh.order.web.dto.resp.WardResp;
import com.bachlinh.order.web.service.common.WardService;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.Collection;

@RouteProvider(name = "wardListWithDistrictHandler")
@ActiveReflection
public class WardListWithDistrictHandler extends AbstractController<Collection<WardResp>, Void> {
    private static final String NOT_EMPTY_MESSAGE_ID = "MSG-000001";

    private String url;
    private WardService wardService;
    private MessageSettingRepository messageSettingRepository;

    private WardListWithDistrictHandler() {
    }

    @Override
    public AbstractController<Collection<WardResp>, Void> newInstance() {
        return new WardListWithDistrictHandler();
    }

    @Override
    @ActiveReflection
    protected Collection<WardResp> internalHandler(Payload<Void> request) {
        String districtId = getNativeRequest().getUrlQueryParam().getFirst("districtId");
        if (!StringUtils.hasText(districtId)) {
            MessageSetting messageSetting = messageSettingRepository.getMessageById(NOT_EMPTY_MESSAGE_ID);
            String errorContent = MessageFormat.format(messageSetting.getValue(), "ID of district");
            throw new BadVariableException(errorContent, getPath());
        }
        if (!ValidateUtils.isNumber(districtId)) {
            MessageSetting notANumberMessage = messageSettingRepository.getMessageById("MSG-000018");
            throw new BadVariableException(MessageFormat.format(notANumberMessage.getValue(), "Id of district"));
        }
        return wardService.getWardsByDistrict(districtId);
    }

    @Override
    protected void inject() {
        var resolver = getContainerResolver().getDependenciesResolver();
        if (wardService == null) {
            wardService = resolver.resolveDependencies(WardService.class);
        }
        if (messageSettingRepository == null) {
            messageSettingRepository = resolver.resolveDependencies(MessageSettingRepository.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.content.ward.list-with-district");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
