package com.bachlinh.order.mail.service;

import com.bachlinh.order.mail.model.GmailMessage;
import com.bachlinh.order.mail.model.GmailSendingResult;
import com.bachlinh.order.mail.oauth2.CredentialAdapter;

import java.io.IOException;
import java.util.Collection;

public interface GmailSendingService {
    GmailSendingResult send(GmailMessage message);

    Collection<GmailSendingResult> sendMulti(Collection<GmailMessage> messages);

    static GmailSendingService defaultService(CredentialAdapter adapter) throws IOException {
        return new DefaultGmailSendingMessage(adapter);
    }
}
