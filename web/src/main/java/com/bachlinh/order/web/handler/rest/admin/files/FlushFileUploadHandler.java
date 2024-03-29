package com.bachlinh.order.web.handler.rest.admin.files;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.annotation.Transactional;
import com.bachlinh.order.core.enums.Isolation;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.exception.system.common.CriticalException;
import com.bachlinh.order.http.Payload;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.common.FileFlushForm;
import com.bachlinh.order.web.service.business.FileUploadService;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Map;

@RouteProvider(name = "flushFileUploadHandler")
@ActiveReflection
public class FlushFileUploadHandler extends AbstractController<Map<String, Object>, FileFlushForm> {
    private String url;
    private FileUploadService fileUploadService;

    private FlushFileUploadHandler() {
        super();
    }

    @Override
    public AbstractController<Map<String, Object>, FileFlushForm> newInstance() {
        return new FlushFileUploadHandler();
    }

    @Override
    @ActiveReflection
    @Transactional(isolation = Isolation.READ_COMMITTED, timeOut = 10)
    protected Map<String, Object> internalHandler(Payload<FileFlushForm> request) {
        try {
            fileUploadService.catAndFlushFile(request.data());
        } catch (IOException e) {
            throw new CriticalException("Can not flush file", e);
        }
        return createDefaultResponse(HttpStatus.CREATED.value(), new String[]{"Create file successfully"});
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
            url = getEnvironment().getProperty("shop.url.admin.files.flush");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.POST;
    }
}
