package com.bachlinh.order.analyzer;

import org.apache.lucene.analysis.CharArraySet;

public class VietnameseConfig {
    private final String dictPath;
    private final CharArraySet stopword;

    public VietnameseConfig(String dictPath, CharArraySet stopword) {
        this.dictPath = dictPath;
        this.stopword = stopword;
    }

    public String getDictPath() {
        return dictPath;
    }

    public CharArraySet getStopword() {
        return stopword;
    }
}
