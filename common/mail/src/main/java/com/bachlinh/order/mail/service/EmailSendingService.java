package com.bachlinh.order.mail.service;

import com.bachlinh.order.mail.model.MessageModel;
import com.bachlinh.order.mail.model.MessageSendingResult;
import com.bachlinh.order.mail.oauth2.CredentialAdapter;

import java.io.IOException;
import java.util.Collection;

public interface EmailSendingService {
    MessageSendingResult send(MessageModel message);

    Collection<MessageSendingResult> sendMulti(Collection<MessageModel> messages);

    static EmailSendingService defaultService(CredentialAdapter adapter) throws IOException {
        return new DefaultEmailSendingMessage(adapter);
    }
}
