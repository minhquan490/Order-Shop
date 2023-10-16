package com.bachlinh.order.mail.service;

import com.bachlinh.order.http.converter.spi.Converter;
import com.bachlinh.order.mail.http.ssl.SslHttpTransportFactory;
import com.bachlinh.order.mail.model.GmailMessage;
import com.bachlinh.order.mail.model.GmailSendingResult;
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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

final class DefaultGmailSendingMessage implements GmailSendingService {
    private final Converter<Message, GmailMessage> converter;
    private final Gmail internalService;

    DefaultGmailSendingMessage(CredentialAdapter adapter) throws IOException, URISyntaxException {
        Credentials credentials = Credentials.basicCredentials(adapter);
        this.converter = new GmailConverter();
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials.getGoogleCredentials());
        this.internalService = new Gmail.Builder(new SslHttpTransportFactory(adapter.getSslPemLocation(), adapter.getSslPrivateKeyLocation()).create(), GsonFactory.getDefaultInstance(), requestInitializer)
                .setApplicationName("Google gmail service")
                .build();
    }

    @Override
    public GmailSendingResult send(GmailMessage message) {
        try {
            Message convertedMessage = converter.convert(message);
            Gmail.Users.Messages.Send send = internalService.users().messages().send(message.getToAddress(), convertedMessage);
            var sentMessage = send.execute();
            GmailSendingResult result = new GmailSendingResult();
            result.setDetail("Send to [" + sentMessage.getId() + "] completed");
            return result;
        } catch (Exception e) {
            if (e instanceof GoogleJsonResponseException casted) {
                return new GmailSendingResult(casted);
            }
            GmailSendingResult result = new GmailSendingResult();
            result.setDetail("Sending email to [" + message.getToAddress() + "] failure");
            result.setStatusCode(HttpStatus.SC_SERVICE_UNAVAILABLE);
            return result;
        }
    }

    @Override
    public Collection<GmailSendingResult> sendMulti(Collection<GmailMessage> messages) {
        List<GmailSendingResult> results = new ArrayList<>(messages.size());
        messages.forEach(message -> results.add(send(message)));
        return results;
    }
}
