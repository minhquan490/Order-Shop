package com.bachlinh.order.web.handler.rest.admin.setting;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.EnableCsrf;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.annotation.Transactional;
import com.bachlinh.order.core.enums.Isolation;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.core.annotation.Permit;
import com.bachlinh.order.core.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.admin.setting.MessageSettingDeleteForm;
import com.bachlinh.order.web.service.common.MessageSettingService;
import org.springframework.http.HttpStatus;

import java.util.Map;

@ActiveReflection
@RouteProvider(name = "messageSettingDeleteHandler")
@Permit(roles = Role.ADMIN)
@EnableCsrf
public class MessageSettingDeleteHandler extends AbstractController<Map<String, Object>, MessageSettingDeleteForm> {

    private String url;
    private MessageSettingService messageSettingService;

    private MessageSettingDeleteHandler() {
    }

    @Override
    public AbstractController<Map<String, Object>, MessageSettingDeleteForm> newInstance() {
        return new MessageSettingDeleteHandler();
    }

    @Override
    @ActiveReflection
    @Transactional(isolation = Isolation.READ_COMMITTED, timeOut = 10)
    protected Map<String, Object> internalHandler(Payload<MessageSettingDeleteForm> request) {
        messageSettingService.deleteMessageSetting(request.data());
        return createDefaultResponse(HttpStatus.ACCEPTED.value(), new String[]{"OK"});
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
            url = getEnvironment().getProperty("shop.url.admin.message-setting.delete");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.DELETE;
    }
}
