package com.bachlinh.order.web.service.common;

import com.bachlinh.order.entity.model.Customer;

public interface EmailFolderService {
    void createDefaultFolders(Customer owner);
}
