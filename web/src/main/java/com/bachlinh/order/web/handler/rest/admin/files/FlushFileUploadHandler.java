package com.bachlinh.order.web.handler.rest.admin.files;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.exception.system.common.CriticalException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.form.common.FileFlushForm;
import com.bachlinh.order.web.service.business.FileUploadService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

@RouteProvider(name = "flushFileUploadHandler")
@ActiveReflection
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FlushFileUploadHandler extends AbstractController<ResponseEntity<?>, FileFlushForm> {
    private String url;
    private FileUploadService fileUploadService;

    @Override
    public AbstractController<ResponseEntity<?>, FileFlushForm> newInstance() {
        return new FlushFileUploadHandler();
    }

    @Override
    @ActiveReflection
    protected ResponseEntity<?> internalHandler(Payload<FileFlushForm> request) {
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
