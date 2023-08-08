package com.bachlinh.order.web.service.common;

import com.bachlinh.order.web.dto.resp.ProductMediaResp;

public interface ProductMediaService {
    
    ProductMediaResp serveResource(String id, long position);

    void deleteMedia(String url);
}
