package com.bachlinh.order.mail.service;

import com.bachlinh.order.core.http.converter.spi.Converter;
import com.bachlinh.order.mail.http.ssl.SslHttpTransportFactory;
import com.bachlinh.order.mail.model.MessageModel;
import com.bachlinh.order.mail.model.MessageSendingResult;
import com.bachlinh.order.mail.model.converter.GmailConverter;
import com.bachlinh.order.mail.oauth2.CredentialAdapter;
import com.bachlinh.order.mail.oauth2.Credentials;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.auth.http.HttpCredentialsAdapter;
import org.apache.http.HttpStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

final class DefaultEmailSendingMessage implements EmailSendingService {
    private final Converter<Message, MessageModel> converter;
    private final Gmail internalService;

    DefaultEmailSendingMessage(CredentialAdapter adapter) throws IOException {
        Credentials credentials = Credentials.basicCredentials(adapter);
        this.converter = new GmailConverter();
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials.getGoogleCredentials());
        this.internalService = new Gmail.Builder(new SslHttpTransportFactory(adapter.getSslPemLocation(), adapter.getSslPrivateKeyLocation()).create(), GsonFactory.getDefaultInstance(), requestInitializer)
                .setApplicationName("Google gmail service")
                .build();
    }

    @Override
    public MessageSendingResult send(MessageModel message) {
        try {
            Message convertedMessage = converter.convert(message);
            Gmail.Users.Messages.Send send = internalService.users().messages().send(message.getToAddress(), convertedMessage);
            MessageSendingResult result = new MessageSendingResult();
            result.setDetail("Send to [" + send.getUserId() + "] completed");
            return result;
        } catch (Exception e) {
            if (e instanceof GoogleJsonResponseException casted) {
                return new MessageSendingResult(casted);
            }
            MessageSendingResult result = new MessageSendingResult();
            result.setDetail("Sending email to [" + message.getToAddress() + "] failure");
            result.setStatusCode(HttpStatus.SC_SERVICE_UNAVAILABLE);
            return result;
        }
    }

    @Override
    public Collection<MessageSendingResult> sendMulti(Collection<MessageModel> messages) {
        List<MessageSendingResult> results = new ArrayList<>(messages.size());
        messages.forEach(message -> results.add(send(message)));
        return results;
    }
}
