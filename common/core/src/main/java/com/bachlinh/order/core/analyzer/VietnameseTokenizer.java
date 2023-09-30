package com.bachlinh.order.core.analyzer;

import com.coccoc.Token;
import com.google.common.base.Objects;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import java.io.IOException;

public class VietnameseTokenizer extends Tokenizer {
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
    private final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);
    private final PositionIncrementAttribute posIncrAtt = addAttribute(PositionIncrementAttribute.class);

    private final VietnameseTokenizerImpl tokenizer;
    private int offset = 0;

    public VietnameseTokenizer(VietnameseConfig config) {
        super();
        tokenizer = new VietnameseTokenizerImpl(config, input);
    }

    @Override
    public boolean incrementToken() throws IOException {
        clearAttributes();
        final Token token = tokenizer.getNextToken();
        if (token != null) {
            posIncrAtt.setPositionIncrement(1);
            typeAtt.setType(String.format("<%s>", token.getType()));
            termAtt.copyBuffer(token.getText().toCharArray(), 0, token.getText().length());
            offsetAtt.setOffset(correctOffset(token.getPos()), offset = correctOffset(token.getEndPos()));
            return true;
        }
        return false;
    }

    @Override
    public final void end() throws IOException {
        super.end();
        final int finalOffset = correctOffset(offset);
        offsetAtt.setOffset(finalOffset, finalOffset);
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        tokenizer.reset(input);
        offset = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        VietnameseTokenizer that = (VietnameseTokenizer) o;
        return offset == that.offset && Objects.equal(termAtt, that.termAtt) && Objects.equal(offsetAtt, that.offsetAtt) && Objects.equal(typeAtt, that.typeAtt) && Objects.equal(posIncrAtt, that.posIncrAtt) && Objects.equal(tokenizer, that.tokenizer);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), termAtt, offsetAtt, typeAtt, posIncrAtt, tokenizer, offset);
    }
}
