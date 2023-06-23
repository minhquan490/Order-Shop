package com.bachlinh.order.web.service.common;

import com.bachlinh.order.service.BaseService;
import com.bachlinh.order.web.dto.form.CategoryForm;
import com.bachlinh.order.web.dto.resp.CategoryResp;

public interface CategoryService extends BaseService<CategoryResp, CategoryForm> {
    boolean isExist(String id);
}
