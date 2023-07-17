package com.bachlinh.order.mail.oauth2;

import java.io.IOException;
import java.io.InputStream;

public interface CredentialAdapter {
    InputStream getCredentialResources() throws IOException;

    String getSslPemLocation();

    String getSslPrivateKeyLocation();

    /**
     * Return scope of Google credentials.
     *
     * @see com.google.api.services.gmail.GmailScopes
     */
    String[] getGmailScope();
}
