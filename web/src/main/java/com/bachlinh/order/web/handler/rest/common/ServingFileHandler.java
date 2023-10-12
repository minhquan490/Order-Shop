package com.bachlinh.order.web.handler.rest.common;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.core.exception.http.BadVariableException;
import com.bachlinh.order.core.exception.http.ResourceNotFoundException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.core.utils.map.LinkedMultiValueMap;
import com.bachlinh.order.web.dto.resp.ProductMediaResp;
import com.bachlinh.order.web.service.common.ProductMediaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MimeType;

@ActiveReflection
@RouteProvider(name = "servingFileHandler")
public class ServingFileHandler extends AbstractController<NativeResponse<byte[]>, Object> {
    private String url;
    private String resourceTotalHeader;
    private ProductMediaService productMediaService;

    private ServingFileHandler() {
        super();
    }

    @Override
    public AbstractController<NativeResponse<byte[]>, Object> newInstance() {
        return new ServingFileHandler();
    }

    @Override
    @ActiveReflection
    protected NativeResponse<byte[]> internalHandler(Payload<Object> request) {
        String resourceId = getNativeRequest().getUrlQueryParam().getFirst("id");
        if (resourceId == null) {
            throw new ResourceNotFoundException("Not found", url);
        }
        String cursor = getNativeRequest().getUrlQueryParam().getFirst("cursor");
        long position;
        try {
            position = cursor == null ? 0 : Long.parseLong(cursor);
        } catch (NumberFormatException e) {
            throw new BadVariableException("Cursor must be number");
        }
        ProductMediaResp resp = productMediaService.serveResource(resourceId, position);
        NativeResponse.NativeResponseBuilder<byte[]> builder = NativeResponse.builder();
        NativeResponse<byte[]> response = builder
                .body(resp.getData())
                .headers(new LinkedMultiValueMap<>())
                .build();
        response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.asMediaType(MimeType.valueOf(resp.getContentType())).toString());
        response.addHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(resp.getData().length));
        response.addHeader(resourceTotalHeader, String.valueOf(resp.getTotalSize()));
        return response;
    }

    @Override
    protected void inject() {
        if (productMediaService == null) {
            productMediaService = resolveService(ProductMediaService.class);
        }
        if (resourceTotalHeader == null) {
            resourceTotalHeader = getEnvironment().getProperty("server.header.resource.total.length");
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.resource");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
