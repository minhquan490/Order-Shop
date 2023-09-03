package com.bachlinh.order.web.handler.rest.admin.setting;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.EnableCsrf;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.Permit;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.admin.setting.MessageSettingDeleteForm;
import com.bachlinh.order.web.service.common.MessageSettingService;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@ActiveReflection
@RouteProvider(name = "messageSettingDeleteHandler")
@NoArgsConstructor(onConstructor = @__({@ActiveReflection}))
@Permit(roles = Role.ADMIN)
@EnableCsrf
public class MessageSettingDeleteHandler extends AbstractController<Map<String, Object>, MessageSettingDeleteForm> {

    private String url;
    private MessageSettingService messageSettingService;

    @Override
    @ActiveReflection
    protected Map<String, Object> internalHandler(Payload<MessageSettingDeleteForm> request) {
        messageSettingService.deleteMessageSetting(request.data());
        var resp = new HashMap<String, Object>(2);
        resp.put("status", HttpStatus.ACCEPTED.value());
        resp.put("messages", new String[]{"OK"});
        return resp;
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
            url = getEnvironment().getProperty("shop.url.admin.message-setting.delete");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.DELETE;
    }
}
