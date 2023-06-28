package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.EmailTrash;

import java.util.Collection;

public interface EmailTrashRepository {
    EmailTrash saveEmailTrash(EmailTrash emailTrash);

    EmailTrash updateTrash(EmailTrash emailTrash);

    void updateTrashes(Collection<EmailTrash> trashes);

    Collection<EmailTrash> getTrashNeedClearing();
}
