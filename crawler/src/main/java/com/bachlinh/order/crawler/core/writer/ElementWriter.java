package com.bachlinh.order.crawler.core.writer;

import com.bachlinh.order.crawler.core.visitor.InnerElementVisitor;

import java.io.IOException;

public interface ElementWriter {
    void write(InnerElementVisitor element) throws IOException;

    void write(byte[] element) throws IOException;

    void write(String element) throws IOException;

    static ElementWriter channelWriter(String filePath) {
        return new ChannelElementWriter(filePath);
    }
}
