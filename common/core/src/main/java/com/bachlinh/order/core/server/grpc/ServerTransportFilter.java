package com.bachlinh.order.core.server.grpc;

import io.grpc.Attributes;

public interface ServerTransportFilter {
    Attributes transportReady(Attributes transportAttrs);

    void transportTerminated(Attributes transportAttrs);

    io.grpc.ServerTransportFilter unwrap();
}
