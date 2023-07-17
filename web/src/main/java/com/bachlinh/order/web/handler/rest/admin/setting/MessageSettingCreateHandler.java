package com.bachlinh.order.web.handler.rest.admin.setting;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.admin.setting.MessageSettingCreateForm;
import com.bachlinh.order.web.dto.resp.MessageSettingResp;
import com.bachlinh.order.web.service.common.MessageSettingService;
import lombok.NoArgsConstructor;

@ActiveReflection
@RouteProvider
@NoArgsConstructor(onConstructor = @__({@ActiveReflection}))
public class MessageSettingCreateHandler extends AbstractController<MessageSettingResp, MessageSettingCreateForm> {
    private String url;
    private MessageSettingService messageSettingService;

    @Override
    @ActiveReflection
    protected MessageSettingResp internalHandler(Payload<MessageSettingCreateForm> request) {
        return messageSettingService.saveMessageSetting(request.data());
    }

    @Override
    protected void inject() {
        var resolver = getContainerResolver().getDependenciesResolver();
        if (messageSettingService == null) {
            messageSettingService = resolver.resolveDependencies(MessageSettingService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.admin.message-setting.create");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.POST;
    }
}