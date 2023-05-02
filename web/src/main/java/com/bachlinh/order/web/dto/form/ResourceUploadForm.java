package com.bachlinh.order.web.dto.form;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("resource")
public record ResourceUploadForm(@JsonAlias("data") String base64Data,
                                 @JsonAlias("file_name") String fileName,
                                 @JsonAlias("total_size") int totalSize) {

}
