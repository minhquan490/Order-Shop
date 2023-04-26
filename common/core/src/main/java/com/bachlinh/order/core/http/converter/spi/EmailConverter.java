package com.bachlinh.order.core.http.converter.spi;

import com.bachlinh.order.core.http.GmailMessage;
import com.google.api.services.gmail.model.Message;

public interface EmailConverter extends Converter<Message, GmailMessage> {
}
