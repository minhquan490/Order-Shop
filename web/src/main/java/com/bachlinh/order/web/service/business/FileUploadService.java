package com.bachlinh.order.web.service.business;

import com.bachlinh.order.web.dto.form.common.FileFlushForm;
import com.bachlinh.order.web.dto.form.common.FileUploadForm;

import java.io.IOException;

public interface FileUploadService {
    void handleMultipartFile(FileUploadForm file) throws IOException;

    void catAndFlushFile(FileFlushForm form) throws IOException;
}
