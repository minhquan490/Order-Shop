package com.bachlinh.order.web.repository.spi;

import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.EmailTrash;
import com.bachlinh.order.repository.NativeQueryRepository;

import java.util.Collection;

public interface EmailTrashRepository extends NativeQueryRepository {
    void saveEmailTrash(EmailTrash emailTrash);

    EmailTrash updateTrash(EmailTrash emailTrash);

    EmailTrash getTrashOfCustomer(Customer customer);

    void updateTrashes(Collection<EmailTrash> trashes);

    void deleteTrash(EmailTrash trash);

    Collection<EmailTrash> getTrashNeedClearing();
}
