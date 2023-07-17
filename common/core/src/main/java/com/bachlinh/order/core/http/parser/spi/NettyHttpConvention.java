package com.bachlinh.order.core.http.parser.spi;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http2.Http2DataFrame;
import io.netty.handler.codec.http2.Http2HeadersFrame;
import io.netty.incubator.codec.http3.Http3DataFrame;
import io.netty.incubator.codec.http3.Http3HeadersFrame;

public interface NettyHttpConvention {
    Http3DataFrame toH3DataFrame();

    Http3HeadersFrame toH3HeaderFrame();

    Http2DataFrame toH2DataFrame();

    Http2HeadersFrame toH2HeaderFrame();

    FullHttpResponse toFullHttpResponse();
}
