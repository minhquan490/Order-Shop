package com.bachlinh.order.web.service.common;

import com.bachlinh.order.web.dto.form.ResourceUploadForm;
import com.bachlinh.order.web.dto.resp.ProductMediaResp;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface ProductMediaService {
    void saveResource(ResourceUploadForm form);

    void deleteResource(String resourceId);

    ProductMediaResp serveResource(String id, long position);

    void write(HttpServletResponse response, byte[] data) throws IOException;
}
