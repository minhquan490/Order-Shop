package com.bachlinh.order.web.handler.rest.common.email.folder;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.EnableCsrf;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.common.EmailFolderDeleteForm;
import com.bachlinh.order.web.service.common.EmailFolderService;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@RouteProvider(name = "emailFolderDeleteHandler")
@ActiveReflection
@EnableCsrf
public class EmailFolderDeleteHandler extends AbstractController<Map<String, Object>, EmailFolderDeleteForm> {
    private EmailFolderService emailFolderService;
    private String url;

    private EmailFolderDeleteHandler() {
    }

    @Override
    public AbstractController<Map<String, Object>, EmailFolderDeleteForm> newInstance() {
        return new EmailFolderDeleteHandler();
    }

    @Override
    @ActiveReflection
    protected Map<String, Object> internalHandler(Payload<EmailFolderDeleteForm> request) {
        emailFolderService.deleteEmailFolder(request.data().getId());
        var resp = new HashMap<String, Object>(2);
        resp.put("status", HttpStatus.ACCEPTED.value());
        resp.put("messages", new String[]{"Delete successfully"});
        return resp;
    }

    @Override
    protected void inject() {
        var resolver = getContainerResolver().getDependenciesResolver();
        if (emailFolderService == null) {
            emailFolderService = resolver.resolveDependencies(EmailFolderService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.content.email-folder.delete");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.DELETE;
    }
}
