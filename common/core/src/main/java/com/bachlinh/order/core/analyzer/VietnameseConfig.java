package com.bachlinh.order.core.analyzer;

import org.apache.lucene.analysis.CharArraySet;

public record VietnameseConfig(String dictPath, CharArraySet stopword) {
}
