package com.bachlinh.order.core.analyzer;

import org.apache.lucene.analysis.CharArraySet;

import java.io.IOException;

public interface StopWordLoader {
    CharArraySet loadStopWord() throws IOException;

    static StopWordLoader defaultLoader() {
        return new DefaultStopWordLoader();
    }

    static StopWordLoader defaultLoader(String stopWordPath) {
        return new DefaultStopWordLoader(stopWordPath);
    }
}
