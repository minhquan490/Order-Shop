package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.DirectMessage;

import java.util.Collection;

public interface DirectMessageRepository extends NativeQueryRepository {
    DirectMessage saveMessage(DirectMessage message);

    DirectMessage updateMessage(DirectMessage message);

    void deleteMessage(int id);

    void deleteMessage(Collection<DirectMessage> directMessages);

    Collection<DirectMessage> getDirectForRemove();
}
