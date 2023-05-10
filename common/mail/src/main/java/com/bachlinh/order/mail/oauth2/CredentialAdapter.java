package com.bachlinh.order.mail.oauth2;

import java.io.FileNotFoundException;
import java.io.InputStream;

public interface CredentialAdapter {
    InputStream getCredentialResources() throws FileNotFoundException;

    String getSslPemLocation();

    String getSslPrivateKeyLocation();

    /**
     * Return scope of Google credentials.
     *
     * @see com.google.api.services.gmail.GmailScopes
     */
    String[] getGmailScope();
}
