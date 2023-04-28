package com.bachlinh.order.security.auth.spi;

import org.springframework.security.oauth2.jose.jws.JwsAlgorithms;

/**
 * Factory for create {@link JwtDecoder} with given settings.
 *
 * @author Hoang Minh Quan
 */
public interface JwtDecoderFactory {

    /**
     * Apply given settings and build the {@link JwtDecoder}.
     *
     * @return built decoder.
     */
    JwtDecoder buildDecoder();

    /**
     * Builder interface for applied settings and build {@link JwtDecoderFactory}
     *
     * @author Hoang Minh Quan
     */
    interface Builder {
        static final String SHA256_ALGORITHM = "HmacSHA256";
        static final String SHA512_ALGORITHM = "HmacSHA512";
        static final String SHA256_HEADER = JwsAlgorithms.HS256;
        static final String SHA512_HEADER = JwsAlgorithms.HS512;

        /**
         * Setting algorithm to use in {@link JwtDecoder}.
         *
         * @return builder for continued building.
         */
        Builder algorithm(String algorithmToUse);

        /**
         * Setting secret key to use in {@link JwtDecoder}.
         *
         * @return builder for continued building.
         */
        Builder secretKey(String secretKeyToUse);

        /**
         * Build the {@link JwtDecoderFactory} for construct {@link JwtDecoder}.
         *
         * @return the factory.
         */
        JwtDecoderFactory build();
    }
}
