package com.bachlinh.order.core.server.grpc;

import io.grpc.stub.StreamObserver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import com.bachlinh.order.core.server.grpc.adapter.ServletRequestAdapter;
import com.bachlinh.order.core.server.grpc.adapter.ServletResponseAdapter;

public abstract class GrpcHandlerAdapter extends GrpcHandlerGrpc.GrpcHandlerImplBase {
    @Override
    public final void handleGrpcCall(InboundMessage request, StreamObserver<OutboundMessage> responseObserver) {
        inject();
        ServletResponseAdapter responseAdapter = getResponse(request.getRequestId());
        ResponseEntity<?> response = processProtoRequest(ServletRequestAdapter.wrap(request), responseAdapter);
        response.getHeaders().forEach((s, strings) -> responseAdapter.setHeader(s, strings.get(0)));
        responseAdapter.setStatus(response.getStatusCode().value());
        responseObserver.onNext(responseAdapter.convert());
        responseObserver.onCompleted();
    }

    protected abstract ResponseEntity<?> processProtoRequest(HttpServletRequest servletRequest, HttpServletResponse servletResponse);

    protected abstract ServletResponseAdapter getResponse(String requestId);

    protected abstract void inject();
}
