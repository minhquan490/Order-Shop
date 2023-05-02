package com.bachlinh.order.web.service.common;

import com.bachlinh.order.service.BaseService;
import com.bachlinh.order.web.dto.form.ResourceUploadForm;
import com.bachlinh.order.web.dto.resp.ProductMediaResp;
import com.bachlinh.order.web.dto.resp.ResourceResp;

public interface ProductMediaService extends BaseService<ResourceResp, ResourceUploadForm> {

    ProductMediaResp serveResource(String id, long position);
}
