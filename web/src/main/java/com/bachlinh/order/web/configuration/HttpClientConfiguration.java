package com.bachlinh.order.web.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.bachlinh.order.core.http.template.internal.DefaultRestTemplateFactory;
import com.bachlinh.order.core.http.template.spi.RestTemplate;
import com.bachlinh.order.core.http.template.spi.RestTemplateFactory;

@Configuration
class HttpClientConfiguration {

    @Bean
    RestTemplate restTemplate(@Value("${server.ssl.certificate}") String certPath, @Value("${server.ssl.certificate-private-key}") String keyPath) throws Exception {
        RestTemplateFactory.Builder builder = DefaultRestTemplateFactory.builder();
        return builder.pemCertificatePath(certPath)
                .pemCertificateKeyPath(keyPath)
                .build()
                .create();
    }
}
