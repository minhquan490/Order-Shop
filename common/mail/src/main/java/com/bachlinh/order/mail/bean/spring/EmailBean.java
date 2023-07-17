package com.bachlinh.order.mail.bean.spring;

import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.mail.oauth2.CredentialAdapter;
import com.bachlinh.order.mail.service.GmailSendingService;
import com.bachlinh.order.mail.template.EmailTemplateProcessor;
import com.google.api.services.gmail.GmailScopes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Configuration
public class EmailBean {

    @Bean
    GmailSendingService emailSendingService(CredentialAdapter credentialAdapter) throws IOException {
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
            public InputStream getCredentialResources() throws IOException {
                return new URL(environment.getProperty("google.email.credentials")).openStream();
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
