package com.bachlinh.order.repository;


import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.EmailFolders;

import java.util.Collection;

public interface EmailFoldersRepository {
    boolean isFolderExisted(String folderName, Customer owner);

    EmailFolders saveEmailFolder(EmailFolders emailFolders);

    EmailFolders getEmailFolderByName(String name, Customer owner);

    EmailFolders getEmailFolderById(String id, Customer owner);

    EmailFolders updateEmailFolder(EmailFolders emailFolders);

    void bulkSave(Collection<EmailFolders> folders);

    void delete(String id);

    Collection<EmailFolders> getEmailFoldersOfCustomer(Customer owner);
}
