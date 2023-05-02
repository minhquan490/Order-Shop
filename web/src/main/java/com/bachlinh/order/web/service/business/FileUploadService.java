package com.bachlinh.order.web.service.business;

import com.bachlinh.order.web.dto.form.FlushFileForm;
import com.bachlinh.order.web.dto.form.ResourceUploadForm;

import java.io.IOException;

public interface FileUploadService {
    void handleMultipartFile(ResourceUploadForm file) throws IOException;

    void catAndFlushFile(FlushFileForm form) throws IOException;
}
