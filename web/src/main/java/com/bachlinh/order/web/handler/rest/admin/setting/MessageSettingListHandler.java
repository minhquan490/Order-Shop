package com.bachlinh.order.web.handler.rest.admin.setting;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.Permit;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.MessageSettingResp;
import com.bachlinh.order.web.service.common.MessageSettingService;

import java.util.Collection;

@ActiveReflection
@RouteProvider(name = "messageSettingListHandler")
@Permit(roles = Role.ADMIN)
public class MessageSettingListHandler extends AbstractController<Collection<MessageSettingResp>, Void> {

    private String url;
    private MessageSettingService messageSettingService;

    private MessageSettingListHandler() {
    }

    @Override
    public AbstractController<Collection<MessageSettingResp>, Void> newInstance() {
        return new MessageSettingListHandler();
    }

    @Override
    @ActiveReflection
    protected Collection<MessageSettingResp> internalHandler(Payload<Void> request) {
        return messageSettingService.getAllMessage();
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
            url = getEnvironment().getProperty("shop.url.admin.message-setting.list");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
