package com.bachlinh.order.web.handler.rest.admin.setting;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.EnableCsrf;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.Permit;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.admin.setting.MessageSettingCreateForm;
import com.bachlinh.order.web.dto.resp.MessageSettingResp;
import com.bachlinh.order.web.service.common.MessageSettingService;

@ActiveReflection
@RouteProvider(name = "messageSettingCreateHandler")
@Permit(roles = Role.ADMIN)
@EnableCsrf
public class MessageSettingCreateHandler extends AbstractController<MessageSettingResp, MessageSettingCreateForm> {
    private String url;
    private MessageSettingService messageSettingService;

    private MessageSettingCreateHandler() {
    }

    @Override
    public AbstractController<MessageSettingResp, MessageSettingCreateForm> newInstance() {
        return new MessageSettingCreateHandler();
    }

    @Override
    @ActiveReflection
    protected MessageSettingResp internalHandler(Payload<MessageSettingCreateForm> request) {
        return messageSettingService.saveMessageSetting(request.data());
    }

    @Override
    protected void inject() {
        if (messageSettingService == null) {
            messageSettingService = resolveService(MessageSettingService.class);
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
