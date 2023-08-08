package com.bachlinh.order.web.dto.resp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductMediaResp {
    private String contentType;
    private byte[] data;
    private long totalSize;
    private boolean isComplete;
}
