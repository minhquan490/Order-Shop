package com.bachlinh.order.repository;


import com.bachlinh.order.entity.model.EmailFolders;

public interface EmailFoldersRepository {
    boolean isFolderExisted(String folderName);

    EmailFolders saveEmailFolder(EmailFolders emailFolders);

    EmailFolders updateEmailFolder(EmailFolders emailFolders);

    void delete(String id);
}
