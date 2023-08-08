package com.bachlinh.order.web.handler.rest.admin.files;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.MultipartRequest;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.exception.system.common.CriticalException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.service.business.FileUploadService;
import lombok.NoArgsConstructor;

import java.io.IOException;

@RouteProvider
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection))
public class AdminFileUploadHandler extends AbstractController<Void, MultipartRequest> {
    private static final Void RETURN_OBJECT = initReturnObject();

    private String url;
    private FileUploadService fileUploadService;

    @Override
    @ActiveReflection
    protected Void internalHandler(Payload<MultipartRequest> request) {
        try {
            fileUploadService.handleFileUpload(request.data());
        } catch (IOException e) {
            throw new CriticalException("Problem when process file upload", e);
        }
        return RETURN_OBJECT;
    }

    @Override
    protected void inject() {
        var resolver = getContainerResolver().getDependenciesResolver();
        if (fileUploadService == null) {
            fileUploadService = resolver.resolveDependencies(FileUploadService.class);
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

    private static Void initReturnObject() {
        try {
            return Void.class.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return null;
        }
    }
}
