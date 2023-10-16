package com.bachlinh.order.http.ssl.spi;

import com.bachlinh.order.http.ssl.internal.SimpleSsl;

public interface Ssl {

    static Ssl simpleInstance() {
        return new SimpleSsl();
    }

    boolean isEnabled();

    void setEnabled(boolean enabled);

    Ssl.ClientAuth getClientAuth();

    void setClientAuth(Ssl.ClientAuth clientAuth);

    String[] getCiphers();

    void setCiphers(String[] ciphers);

    String[] getEnabledProtocols();

    void setEnabledProtocols(String[] enabledProtocols);

    String getKeyAlias();

    void setKeyAlias(String keyAlias);

    String getKeyPassword();

    void setKeyPassword(String keyPassword);

    String getKeyStore();

    void setKeyStore(String keyStore);

    String getKeyStorePassword();

    void setKeyStorePassword(String keyStorePassword);

    String getKeyStoreType();

    void setKeyStoreType(String keyStoreType);

    String getKeyStoreProvider();

    void setKeyStoreProvider(String keyStoreProvider);

    String getTrustStore();

    void setTrustStore(String trustStore);

    String getTrustStorePassword();

    void setTrustStorePassword(String trustStorePassword);

    String getTrustStoreType();

    void setTrustStoreType(String trustStoreType);

    String getTrustStoreProvider();

    void setTrustStoreProvider(String trustStoreProvider);

    String getCertificate();

    void setCertificate(String certificate);

    String getCertificatePrivateKey();

    void setCertificatePrivateKey(String certificatePrivateKey);

    String getTrustCertificate();

    void setTrustCertificate(String trustCertificate);

    String getTrustCertificatePrivateKey();

    void setTrustCertificatePrivateKey(String trustCertificatePrivateKey);

    String getProtocol();

    void setProtocol(String protocol);

    public enum ClientAuth {
        NONE,
        WANT,
        NEED
    }
}
