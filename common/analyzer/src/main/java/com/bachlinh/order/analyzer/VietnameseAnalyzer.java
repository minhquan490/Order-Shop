package com.bachlinh.order.analyzer;

import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;

public class VietnameseAnalyzer extends StopwordAnalyzerBase {
    private final VietnameseConfig config;

    public VietnameseAnalyzer(VietnameseConfig config) {
        super(config.stopword());
        this.config = config;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        final Tokenizer source = new VietnameseTokenizer(config);
        TokenStream result = new LowerCaseFilter(source);
        result = new StopFilter(result, stopwords);
        return new TokenStreamComponents(source, result);
    }
}
