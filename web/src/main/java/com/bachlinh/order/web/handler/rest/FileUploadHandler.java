package com.bachlinh.order.web.handler.rest;

import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.exception.system.common.CriticalException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.form.ResourceUploadForm;
import com.bachlinh.order.web.service.business.FileUploadService;

import java.io.IOException;

@ActiveReflection
@RouteProvider
@NoArgsConstructor(onConstructor_ = @ActiveReflection)
public class FileUploadHandler extends AbstractController<ResponseEntity<?>, ResourceUploadForm> {
    private String url;
    private FileUploadService fileUploadService;

    @Override
    @ActiveReflection
    protected ResponseEntity<?> internalHandler(Payload<ResourceUploadForm> request) {
        try {
            fileUploadService.handleMultipartFile(request.data());
        } catch (IOException e) {
            throw new CriticalException("Handle file upload failure", e);
        }
        return ResponseEntity.accepted().build();
    }

    @Override
    protected void inject() {
        DependenciesResolver resolver = getContainerResolver().getDependenciesResolver();
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
}
