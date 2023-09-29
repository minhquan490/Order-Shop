package com.bachlinh.order.web.handler.rest.common.address;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.exception.http.BadVariableException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.utils.ValidateUtils;
import com.bachlinh.order.web.dto.resp.DistrictResp;
import com.bachlinh.order.web.dto.resp.MessageSettingResp;
import com.bachlinh.order.web.service.common.DistrictService;
import com.bachlinh.order.web.service.common.MessageSettingService;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.Collection;

@ActiveReflection
@RouteProvider(name = "districtListWithProvinceHandler")
public class DistrictListWithProvinceHandler extends AbstractController<Collection<DistrictResp>, Void> {

    private String url;
    private DistrictService districtService;
    private MessageSettingService messageSettingService;

    private DistrictListWithProvinceHandler() {
    }

    @Override
    public AbstractController<Collection<DistrictResp>, Void> newInstance() {
        return new DistrictListWithProvinceHandler();
    }

    @Override
    @ActiveReflection
    protected Collection<DistrictResp> internalHandler(Payload<Void> request) {
        String provinceId = getNativeRequest().getUrlQueryParam().getFirst("provinceId");
        if (!StringUtils.hasText(provinceId)) {
            throw new BadVariableException("Province id is required", getPath());
        }
        if (!ValidateUtils.isNumber(provinceId)) {
            MessageSettingResp notANumberMessage = messageSettingService.getMessage("MSG-000018");
            throw new BadVariableException(MessageFormat.format(notANumberMessage.getValue(), "Id of province"));
        }
        return districtService.getDistrictByProvince(provinceId);
    }

    @Override
    protected void inject() {
        if (districtService == null) {
            districtService = resolveService(DistrictService.class);
        }
        if (messageSettingService == null) {
            messageSettingService = resolveService(MessageSettingService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.content.district.list-with-province");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
