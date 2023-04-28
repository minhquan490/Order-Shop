package com.bachlinh.order.security.auth.internal;

import com.bachlinh.order.security.auth.spi.JwtDecoderFactory;
import com.bachlinh.order.security.auth.spi.JwtEncoderFactory;
import org.springframework.util.Assert;

public final class JwtFactoryBuilderProvider {
    private static final String DEFAULT_ALGORITHM = JwtDecoderFactory.Builder.SHA256_ALGORITHM;

    private JwtFactoryBuilderProvider() {
        throw new UnsupportedOperationException("Can not instance JwtFactoryBuilderProvider");
    }

    public static JwtDecoderFactory.Builder provideJwtDecoderFactoryBuilder() {
        return new JwtDecoderFactoryBuilder();
    }

    public static JwtEncoderFactory.Builder provideJwtEncoderFactoryBuilder() {
        return new JwtEncoderFactoryBuilder();
    }

    private static class JwtDecoderFactoryBuilder implements JwtDecoderFactory.Builder {
        private String algorithmToUse;
        private String secretKeyToUse;

        @Override
        public JwtDecoderFactory.Builder algorithm(String algorithmToUse) {
            this.algorithmToUse = algorithmToUse;
            return this;
        }

        @Override
        public JwtDecoderFactory.Builder secretKey(String secretKeyToUse) {
            this.secretKeyToUse = secretKeyToUse;
            return this;
        }

        @Override
        public JwtDecoderFactory build() {
            Assert.notNull(secretKeyToUse, "Secret key can not be null");
            return new JwtDecoderFactoryImpl(algorithmToUse == null ? DEFAULT_ALGORITHM : algorithmToUse, secretKeyToUse);
        }
    }

    private static class JwtEncoderFactoryBuilder implements JwtEncoderFactory.Builder {
        private String secretKeyToUse;
        private String algorithmToUse;

        @Override
        public JwtEncoderFactory.Builder algorithm(String algorithmToUse) {
            this.algorithmToUse = algorithmToUse;
            return this;
        }

        @Override
        public JwtEncoderFactory.Builder secretKey(String secretKeyToUse) {
            this.secretKeyToUse = secretKeyToUse;
            return this;
        }

        @Override
        public JwtEncoderFactory build() {
            Assert.notNull(secretKeyToUse, "Secret key can not be null");
            return new JwtEncoderFactoryImpl(algorithmToUse == null ? DEFAULT_ALGORITHM : algorithmToUse, secretKeyToUse);
        }

    }
}
