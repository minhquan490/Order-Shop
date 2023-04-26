package com.bachlinh.order.core.http.ssl.internal;

import com.bachlinh.order.core.http.ssl.spi.Ssl;

public class SimpleSsl implements Ssl {
    private boolean enabled = true;

    private Ssl.ClientAuth clientAuth;

    private String[] ciphers;

    private String[] enabledProtocols;

    private String keyAlias;

    private String keyPassword;

    private String keyStore;

    private String keyStorePassword;

    private String keyStoreType;

    private String keyStoreProvider;

    private String trustStore;

    private String trustStorePassword;

    private String trustStoreType;

    private String trustStoreProvider;

    private String certificate;

    private String certificatePrivateKey;

    private String trustCertificate;

    private String trustCertificatePrivateKey;

    private String protocol = "TLS";

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Ssl.ClientAuth getClientAuth() {
        return this.clientAuth;
    }

    @Override
    public void setClientAuth(Ssl.ClientAuth clientAuth) {
        this.clientAuth = clientAuth;
    }

    @Override
    public String[] getCiphers() {
        return this.ciphers;
    }

    @Override
    public void setCiphers(String[] ciphers) {
        this.ciphers = ciphers;
    }

    @Override
    public String[] getEnabledProtocols() {
        return this.enabledProtocols;
    }

    @Override
    public void setEnabledProtocols(String[] enabledProtocols) {
        this.enabledProtocols = enabledProtocols;
    }

    @Override
    public String getKeyAlias() {
        return this.keyAlias;
    }

    @Override
    public void setKeyAlias(String keyAlias) {
        this.keyAlias = keyAlias;
    }

    @Override
    public String getKeyPassword() {
        return this.keyPassword;
    }

    @Override
    public void setKeyPassword(String keyPassword) {
        this.keyPassword = keyPassword;
    }

    @Override
    public String getKeyStore() {
        return this.keyStore;
    }

    @Override
    public void setKeyStore(String keyStore) {
        this.keyStore = keyStore;
    }

    @Override
    public String getKeyStorePassword() {
        return this.keyStorePassword;
    }

    @Override
    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    @Override
    public String getKeyStoreType() {
        return this.keyStoreType;
    }

    @Override
    public void setKeyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }

    @Override
    public String getKeyStoreProvider() {
        return this.keyStoreProvider;
    }

    @Override
    public void setKeyStoreProvider(String keyStoreProvider) {
        this.keyStoreProvider = keyStoreProvider;
    }

    @Override
    public String getTrustStore() {
        return this.trustStore;
    }

    @Override
    public void setTrustStore(String trustStore) {
        this.trustStore = trustStore;
    }

    @Override
    public String getTrustStorePassword() {
        return this.trustStorePassword;
    }

    @Override
    public void setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }

    @Override
    public String getTrustStoreType() {
        return this.trustStoreType;
    }

    @Override
    public void setTrustStoreType(String trustStoreType) {
        this.trustStoreType = trustStoreType;
    }

    @Override
    public String getTrustStoreProvider() {
        return this.trustStoreProvider;
    }

    @Override
    public void setTrustStoreProvider(String trustStoreProvider) {
        this.trustStoreProvider = trustStoreProvider;
    }

    @Override
    public String getCertificate() {
        return this.certificate;
    }

    @Override
    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    @Override
    public String getCertificatePrivateKey() {
        return this.certificatePrivateKey;
    }

    @Override
    public void setCertificatePrivateKey(String certificatePrivateKey) {
        this.certificatePrivateKey = certificatePrivateKey;
    }

    @Override
    public String getTrustCertificate() {
        return this.trustCertificate;
    }

    @Override
    public void setTrustCertificate(String trustCertificate) {
        this.trustCertificate = trustCertificate;
    }

    @Override
    public String getTrustCertificatePrivateKey() {
        return this.trustCertificatePrivateKey;
    }

    @Override
    public void setTrustCertificatePrivateKey(String trustCertificatePrivateKey) {
        this.trustCertificatePrivateKey = trustCertificatePrivateKey;
    }

    @Override
    public String getProtocol() {
        return this.protocol;
    }

    @Override
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
