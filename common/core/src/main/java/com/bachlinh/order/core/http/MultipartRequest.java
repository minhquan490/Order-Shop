package com.bachlinh.order.core.http;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MultipartRequest {
    private String fileName;
    private String contentType;
    private long contentLength;
    private boolean isChunked;
    private byte[] bodyContent;
}
