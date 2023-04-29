package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.DirectMessage;

public interface DirectMessageRepository {
    DirectMessage saveMessage(DirectMessage message);

    DirectMessage updateMessage(DirectMessage message);

    void deleteMessage(int id);
}
