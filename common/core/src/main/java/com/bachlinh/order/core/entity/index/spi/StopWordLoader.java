package com.bachlinh.order.core.entity.index.spi;

import org.apache.lucene.analysis.CharArraySet;

import java.io.IOException;

public interface StopWordLoader {
    CharArraySet loadStopWord() throws IOException;
}
