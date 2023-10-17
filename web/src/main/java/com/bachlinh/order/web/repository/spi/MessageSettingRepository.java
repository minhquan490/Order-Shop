package com.bachlinh.order.web.repository.spi;

import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.repository.NativeQueryRepository;

import java.util.Collection;

public interface MessageSettingRepository extends NativeQueryRepository {
    MessageSetting saveMessage(MessageSetting messageSetting);

    MessageSetting updateMessage(MessageSetting messageSetting);

    MessageSetting getMessageById(String id);

    void saveMessages(Collection<MessageSetting> messageSettings);

    void deleteMessage(String messageId);

    Collection<MessageSetting> getMessages();

    long countMessages();

    boolean messageValueExisted(String messageValue);

    boolean isMessageSettingExisted(String id);
}
