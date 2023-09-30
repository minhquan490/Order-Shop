package com.bachlinh.order.core.analyzer;

import com.bachlinh.order.core.exception.system.search.LuceneException;
import org.apache.lucene.analysis.CharArraySet;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Stream;

class DefaultStopWordLoader implements StopWordLoader {
    private CharArraySet cachingStopWord;
    private String stopwordPath;

    DefaultStopWordLoader(String stopwordPath) {
        this.stopwordPath = stopwordPath;
    }

    DefaultStopWordLoader() {
        this(null);
    }

    @Override
    public CharArraySet loadStopWord() throws IOException {
        if (cachingStopWord == null) {
            if (stopwordPath == null) {
                ClassLoader classLoader = ClassLoader.getSystemClassLoader();
                URL stopwordURL = classLoader.getResource("stop-word.txt");
                if (stopwordURL == null) {
                    throw new LuceneException("Can not determine stopword file");
                }
                stopwordPath = stopwordURL.getPath();
            }
            if (stopwordPath.startsWith("jar")) {
                cachingStopWord = loadSystemStopword();
            } else {
                FileChannel channel = openChannel(stopwordPath);
                Collection<Byte> bytes = load(channel, new ArrayList<>());
                cachingStopWord = parseBuffer(bytes);
                channel.close();
            }
            return cachingStopWord;
        }
        return cachingStopWord;
    }

    private FileChannel openChannel(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            throw new NoSuchElementException("Can find file with path [" + path + "]");
        }
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        return randomAccessFile.getChannel();
    }

    private Collection<Byte> load(FileChannel channel, Collection<Byte> data) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1);
        while (channel.read(buffer) > 0) {
            data.add(buffer.array()[0]);
            load(channel, data);
        }
        return data;
    }

    private CharArraySet parseBuffer(Collection<Byte> buffer) {
        byte[] data = new byte[buffer.size()];
        int count = 0;
        for (Byte buf : buffer) {
            data[count] = buf;
            count++;
        }
        buffer.clear();
        return parseBuffer(data);
    }

    private CharArraySet parseBuffer(byte[] buffer) {
        String keyword = new String(buffer, StandardCharsets.UTF_8);
        String[] keys = keyword.split(System.lineSeparator());
        Set<String> keySet = new HashSet<>();
        for (String key : keys) {
            keySet.addAll(Stream.of(key.split(",")).toList());
        }
        return new CharArraySet(keySet, true);
    }

    private CharArraySet loadSystemStopword() {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        try {
            URL resourceUrl = resourceLoader.getResource("classpath:stop-word.txt").getURL();
            InputStream inputStream = (InputStream) resourceUrl.getContent();
            return parseBuffer(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new LuceneException("Can not read stop word");
        }
    }
}
