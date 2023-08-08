package com.bachlinh.order.analyzer;

import org.apache.lucene.analysis.CharArraySet;

public record VietnameseConfig(String dictPath, CharArraySet stopword) {
}
