package com.bachlinh.order.web.handler.rest.admin.setting;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.Permit;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.admin.setting.MessageSettingUpdateForm;
import com.bachlinh.order.web.dto.resp.MessageSettingResp;
import com.bachlinh.order.web.service.common.MessageSettingService;
import lombok.NoArgsConstructor;

@ActiveReflection
@RouteProvider(name = "messageSettingUpdateHandler")
@NoArgsConstructor(onConstructor = @__({@ActiveReflection}))
@Permit(roles = Role.ADMIN)
public class MessageSettingUpdateHandler extends AbstractController<MessageSettingResp, MessageSettingUpdateForm> {

    private String url;
    private MessageSettingService messageSettingService;

    @Override
    @ActiveReflection
    protected MessageSettingResp internalHandler(Payload<MessageSettingUpdateForm> request) {
        return messageSettingService.updateMessageSetting(request.data());
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
            url = getEnvironment().getProperty("shop.url.admin.message-setting.update");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.PATCH;
    }
}
