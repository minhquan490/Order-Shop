package com.bachlinh.order.mail.oauth2;

import com.bachlinh.order.mail.http.ssl.SslHttpTransportFactory;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;

class BasicCredentials implements Credentials {
    private final CredentialAdapter adapter;

    BasicCredentials(CredentialAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public GoogleCredentials getGoogleCredentials() throws IOException {
        return GoogleCredentials.fromStream(adapter.getCredentialResources(), new SslHttpTransportFactory(adapter.getSslPemLocation(), adapter.getSslPrivateKeyLocation()))
                .createScoped(adapter.getGmailScope());
    }
}
