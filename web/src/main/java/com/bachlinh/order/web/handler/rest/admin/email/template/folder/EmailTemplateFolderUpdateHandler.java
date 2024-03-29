package com.bachlinh.order.web.handler.rest.admin.email.template.folder;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.EnableCsrf;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.annotation.Transactional;
import com.bachlinh.order.core.enums.Isolation;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.http.Payload;
import com.bachlinh.order.core.annotation.Permit;
import com.bachlinh.order.core.enums.Role;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.admin.email.template.folder.EmailTemplateFolderUpdateForm;
import com.bachlinh.order.web.dto.resp.EmailTemplateFolderInfoResp;
import com.bachlinh.order.web.service.common.EmailTemplateFolderService;
import org.springframework.security.core.context.SecurityContextHolder;

@RouteProvider(name = "emailTemplateFolderUpdateHandler")
@ActiveReflection
@EnableCsrf
@Permit(roles = {Role.ADMIN, Role.SEO, Role.MARKETING})
public class EmailTemplateFolderUpdateHandler extends AbstractController<EmailTemplateFolderInfoResp, EmailTemplateFolderUpdateForm> {
    private EmailTemplateFolderService emailTemplateFolderService;
    private String url;

    private EmailTemplateFolderUpdateHandler() {
        super();
    }

    @Override
    public AbstractController<EmailTemplateFolderInfoResp, EmailTemplateFolderUpdateForm> newInstance() {
        return new EmailTemplateFolderUpdateHandler();
    }

    @Override
    @ActiveReflection
    @Transactional(isolation = Isolation.READ_COMMITTED, timeOut = 10)
    protected EmailTemplateFolderInfoResp internalHandler(Payload<EmailTemplateFolderUpdateForm> request) {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return emailTemplateFolderService.updateEmailTemplateFolder(request.data(), customer);
    }

    @Override
    protected void inject() {
        if (emailTemplateFolderService == null) {
            emailTemplateFolderService = resolveService(EmailTemplateFolderService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.admin.email-template-folder.update");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.PATCH;
    }
}
