package com.bachlinh.order.analyzer;

import com.coccoc.Token;
import com.coccoc.Tokenizer;
import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

final class VietnameseTokenizerImpl {
    private final Tokenizer.TokenizeOption option;
    private final Tokenizer tokenizer;
    private final List<Token> pending;
    private Reader input;
    private int pos = -1;

    VietnameseTokenizerImpl(VietnameseConfig config, Reader input) {
        this.input = input;
        this.option = Tokenizer.TokenizeOption.NORMAL;
        tokenizer = Tokenizer.getInstance(config.getDictPath());
        pending = new CopyOnWriteArrayList<>();
    }

    public Token getNextToken() throws IOException {
        while (pending.isEmpty()) {
            tokenize();
            if (pending.isEmpty()) {
                return null;
            }
        }
        pos++;
        return pos < pending.size() ? pending.get(pos) : null;
    }

    public void reset(Reader input) {
        this.input = input;
        pending.clear();
        pos = -1;
    }


    private void tokenize() throws IOException {
        final List<Token> tokens = tokenize(input);
        if (tokens != null) {
            pending.addAll(tokens);
        }
    }

    private List<Token> tokenize(Reader input) throws IOException {
        return tokenize(CharStreams.toString(input));
    }


    private List<Token> tokenize(String input) {
        return tokenizer.segment(input, option, true);
    }

}
