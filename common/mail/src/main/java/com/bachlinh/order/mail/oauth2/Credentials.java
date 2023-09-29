package com.bachlinh.order.mail.oauth2;

import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.net.URISyntaxException;

public interface Credentials {
    GoogleCredentials getGoogleCredentials() throws IOException, URISyntaxException;

    static Credentials basicCredentials(CredentialAdapter adapter) {
        return new BasicCredentials(adapter);
    }
}
