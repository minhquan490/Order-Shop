package com.bachlinh.order.web.service.business;

import com.bachlinh.order.core.http.MultipartRequest;
import com.bachlinh.order.web.dto.form.common.FileFlushForm;

import java.io.IOException;

public interface FileUploadService {

    void catAndFlushFile(FileFlushForm form) throws IOException;

    void handleFileUpload(MultipartRequest multipartRequest) throws IOException;
}
