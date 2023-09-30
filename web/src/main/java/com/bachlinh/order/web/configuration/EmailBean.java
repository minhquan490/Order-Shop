package com.bachlinh.order.web.configuration;

import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.mail.oauth2.CredentialAdapter;
import com.bachlinh.order.mail.service.GmailSendingService;
import com.bachlinh.order.mail.template.EmailTemplateProcessor;
import com.google.api.services.gmail.GmailScopes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class EmailBean {

    @Bean
    GmailSendingService emailSendingService(CredentialAdapter credentialAdapter) throws IOException, URISyntaxException {
        return GmailSendingService.defaultService(credentialAdapter);
    }

    @Bean
    EmailTemplateProcessor processor() {
        return EmailTemplateProcessor.defaultInstance();
    }

    @Bean
    CredentialAdapter credentialAdapter(@Value("${active.profile}") String profile) {
        Environment environment = Environment.getInstance(profile);
        return new CredentialAdapter() {
            @Override
            public InputStream getCredentialResources() throws IOException, URISyntaxException {
                return new URI(environment.getProperty("google.email.credentials")).toURL().openStream();
            }

            @Override
            public String getSslPemLocation() {
                return environment.getProperty("server.ssl.certificate");
            }

            @Override
            public String getSslPrivateKeyLocation() {
                return environment.getProperty("server.ssl.certificate-private-key");
            }

            @Override
            public String[] getGmailScope() {
                return new String[]{GmailScopes.MAIL_GOOGLE_COM};
            }
        };
    }
}
