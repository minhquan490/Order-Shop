package com.bachlinh.order.http.client;

public interface RestClientFactory {

    /**
     * Create {@code RestTemplate} with given options.
     *
     * @return Built {@code RestTemplate} ready for use.
     * @throws Exception If problem occur when building {@code RestTemplate}.
     */
    RestClient create() throws Exception;

    /**
     * The builder for create {@link RestClientFactory}, the factory will pick up options
     * pass by this builder use for create {@code RestTemplate}.
     *
     * @author Hoang Minh Quan
     */
    interface Builder {

        /**
         * Setup certificate with pem format from given location.
         *
         * @param path Location of pem certificate.
         * @return This builder for continue building.
         */
        Builder pemCertificatePath(String path);

        /**
         * Setup certificate private key with pem format from given location.
         *
         * @param path Location of pem certificate private key.
         * @return This builder for continue building.
         */
        Builder pemCertificateKeyPath(String path);

        Builder pemCertificatePassword(String password);

        /**
         * Build {@link RestClientFactory} with above options.
         *
         * @return Completed {@code RestTemplateFactory} ready for use.
         */
        RestClientFactory build();
    }
}
