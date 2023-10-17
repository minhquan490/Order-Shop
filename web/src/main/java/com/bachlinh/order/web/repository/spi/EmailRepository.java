package com.bachlinh.order.web.repository.spi;

import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Email;
import com.bachlinh.order.repository.NativeQueryRepository;

import java.util.Collection;
import java.util.List;

public interface EmailRepository extends NativeQueryRepository {
    Email saveEmail(Email email);

    Email getEmailOfCustomerById(String id, Customer owner);

    Email getEmailForAddToTrash(String id, Customer owner);

    Collection<Email> getAllEmailByIds(Iterable<String> ids);

    Collection<Email> getEmailsForAddToTrash(Iterable<String> ids, Customer owner);

    List<Email> getEmailsByFolderId(String folderId, Customer owner);

    Collection<Email> getResultSearch(Iterable<String> ids, Customer owner);

    Collection<Email> getEmailForRestore(Integer emailTrashId, Iterable<String> ids);

    Collection<Email> getEmailsOfCustomer(Customer owner);

    void deleteEmails(Collection<String> ids);

    void deleteEmail(Collection<Email> emails);
}
