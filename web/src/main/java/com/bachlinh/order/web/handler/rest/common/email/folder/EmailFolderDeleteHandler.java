package com.bachlinh.order.web.handler.rest.common.email.folder;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.EnableCsrf;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.annotation.Transactional;
import com.bachlinh.order.core.enums.Isolation;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.common.EmailFolderDeleteForm;
import com.bachlinh.order.web.service.common.EmailFolderService;
import org.springframework.http.HttpStatus;

import java.util.Map;

@RouteProvider(name = "emailFolderDeleteHandler")
@ActiveReflection
@EnableCsrf
public class EmailFolderDeleteHandler extends AbstractController<Map<String, Object>, EmailFolderDeleteForm> {
    private EmailFolderService emailFolderService;
    private String url;

    private EmailFolderDeleteHandler() {
        super();
    }

    @Override
    public AbstractController<Map<String, Object>, EmailFolderDeleteForm> newInstance() {
        return new EmailFolderDeleteHandler();
    }

    @Override
    @ActiveReflection
    @Transactional(isolation = Isolation.READ_COMMITTED, timeOut = 10)
    protected Map<String, Object> internalHandler(Payload<EmailFolderDeleteForm> request) {
        emailFolderService.deleteEmailFolder(request.data().getId());
        return createDefaultResponse(HttpStatus.ACCEPTED.value(), new String[]{"Delete successfully"});
    }

    @Override
    protected void inject() {
        if (emailFolderService == null) {
            emailFolderService = resolveService(EmailFolderService.class);
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
