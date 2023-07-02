package com.bachlinh.order.web.dto.form.common;

import com.fasterxml.jackson.annotation.JsonAlias;

public record FileUploadForm(@JsonAlias("data") String base64Data,
                             @JsonAlias("file_name") String fileName,
                             @JsonAlias("total_size") int totalSize) {

}
