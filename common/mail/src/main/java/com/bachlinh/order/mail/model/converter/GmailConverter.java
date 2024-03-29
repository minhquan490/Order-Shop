package com.bachlinh.order.mail.model.converter;

import com.google.api.services.gmail.model.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.codec.binary.Base64;
import static jakarta.mail.Message.RecipientType.TO;
import static java.text.MessageFormat.format;
import com.bachlinh.order.http.converter.spi.Converter;
import com.bachlinh.order.core.exception.system.mail.MailException;
import com.bachlinh.order.mail.model.GmailMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

public class GmailConverter implements Converter<Message, GmailMessage> {
    private static final String EMAIL_TYPE_PATTERN = "{0}; charset={1}";

    @Override
    public Message convert(GmailMessage message) {
        try {
            Session session = Session.getDefaultInstance(new Properties());
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(message.getFromAddress()));
            mimeMessage.addRecipient(TO, new InternetAddress(message.getToAddress()));
            mimeMessage.setSubject(message.getSubject());
            mimeMessage.setContent(message.getBody(), format(EMAIL_TYPE_PATTERN, message.getContentType(), message.getCharset().displayName()));

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            mimeMessage.writeTo(byteArrayOutputStream);

            Message m = new Message();
            m.setRaw(Base64.encodeBase64URLSafeString(byteArrayOutputStream.toByteArray()));
            return m;
        } catch (MessagingException | IOException e) {
            throw new MailException("Can not send email", e);
        }
    }
}
