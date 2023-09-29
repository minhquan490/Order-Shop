package com.bachlinh.order.web.handler.rest.admin.files;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.EnableCsrf;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.MultipartRequest;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.Permit;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.exception.system.common.CriticalException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.service.business.FileUploadService;
import org.apache.http.HttpStatus;

import java.io.IOException;
import java.util.Map;

@RouteProvider
@ActiveReflection
@EnableCsrf
@Permit(roles = Role.ADMIN)
public class AdminFileUploadHandler extends AbstractController<Map<String, Object>, MultipartRequest> {

    private String url;
    private FileUploadService fileUploadService;

    private AdminFileUploadHandler() {
    }

    @Override
    public AbstractController<Map<String, Object>, MultipartRequest> newInstance() {
        return new AdminFileUploadHandler();
    }

    @Override
    @ActiveReflection
    protected Map<String, Object> internalHandler(Payload<MultipartRequest> request) {
        try {
            fileUploadService.handleFileUpload(request.data());
        } catch (IOException e) {
            throw new CriticalException("Problem when process file upload", e);
        }
        return createDefaultResponse(HttpStatus.SC_OK, new String[]{"Upload successfully"});
    }

    @Override
    protected void inject() {
        if (fileUploadService == null) {
            fileUploadService = resolveService(FileUploadService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.admin.files.upload");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.POST;
    }
}
