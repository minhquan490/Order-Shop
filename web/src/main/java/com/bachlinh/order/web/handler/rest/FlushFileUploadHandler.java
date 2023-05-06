package com.bachlinh.order.web.handler.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.exception.system.common.CriticalException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.form.FlushFileForm;
import com.bachlinh.order.web.service.business.FileUploadService;

import java.io.IOException;

@RouteProvider
@ActiveReflection
public class FlushFileUploadHandler extends AbstractController<ResponseEntity<?>, FlushFileForm> {
    private String url;
    private FileUploadService fileUploadService;

    @ActiveReflection
    public FlushFileUploadHandler() {
    }

    @Override
    protected ResponseEntity<?> internalHandler(Payload<FlushFileForm> request) {
        try {
            fileUploadService.catAndFlushFile(request.data());
        } catch (IOException e) {
            throw new CriticalException("Can not flush file", e);
        }
        return ResponseEntity.status(HttpStatus.CREATED.value()).build();
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
            url = getEnvironment().getProperty("shop.url.admin.files.flush");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.POST;
    }
}
