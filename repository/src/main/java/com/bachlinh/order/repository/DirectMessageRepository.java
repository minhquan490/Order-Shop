package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.DirectMessage;
import com.bachlinh.order.entity.repository.NativeQueryRepository;

import java.util.Collection;

public interface DirectMessageRepository extends NativeQueryRepository {
    void saveMessage(DirectMessage message);

    void deleteMessage(Collection<DirectMessage> directMessages);

    Collection<DirectMessage> getDirectForRemove();
}
