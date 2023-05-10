package com.bachlinh.order.crawler.core.writer;

import com.bachlinh.order.crawler.core.visitor.InnerElementVisitor;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

class ChannelElementWriter implements ElementWriter {
    private static final String FILE_MODE = "rw";
    private final String path;
    private final ByteBufferConverter converter;

    ChannelElementWriter(String flushableFilePath) {
        path = flushableFilePath;
        converter = new ByteBufferConverter();
    }

    @Override
    public void write(InnerElementVisitor visitor) throws IOException {
        ByteBuffer byteBuffer = converter.convert(visitor);
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(path, FILE_MODE);
             FileChannel channel = randomAccessFile.getChannel()) {
            channel.write(byteBuffer);
        } finally {
            byteBuffer.clear();
        }
    }

    @Override
    public void write(byte[] element) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.wrap(element);
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(path, FILE_MODE);
             FileChannel channel = randomAccessFile.getChannel()) {
            channel.write(byteBuffer);
        } finally {
            byteBuffer.clear();
        }
    }

    @Override
    public void write(String element) throws IOException {
        write(element.getBytes(StandardCharsets.UTF_8));
    }
}
