package com.bachlinh.order.security.auth.spi;

/**
 * Factory interface for create {@link JwtDecoder}.
 *
 * @author Hoang Minh Quan
 */
public interface JwtEncoderFactory {

    /**
     * Build {@link JwtDecoder} with settings for used.
     *
     * @return the encoder.
     */
    JwtEncoder buildEncoder();

    /**
     * Builder interface for applied settings to construct {@link JwtEncoderFactory}.
     *
     * @author Hoang Minh Quan.
     */
    interface Builder {

        /**
         * Apply algorithm to use in the encoder.
         *
         * @param algorithmToUse algorithm for use.
         * @return builder for continued building.
         */
        Builder algorithm(String algorithmToUse);

        /**
         * Apply secret key to use in the encoder.
         *
         * @param secretKeyToUse secret key for use.
         * @return builder for continued building.
         */
        Builder secretKey(String secretKeyToUse);

        /**
         * Construct the {@link JwtEncoderFactory} for used.
         *
         * @return the encoder factory.
         */
        JwtEncoderFactory build();
    }
}
