package com.bachlinh.order.web.dto.form;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("flush")
public record FlushFileForm(@JsonAlias("file_id") String fileId,
                            @JsonAlias("product_id") String productId,
                            @JsonAlias("content_type") String contentType) {
}
