package com.bachlinh.order.core.server.grpc;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import com.bachlinh.order.core.server.grpc.adapter.ServletRequestAdapter;
import com.bachlinh.order.core.server.grpc.adapter.ServletResponseAdapter;

public abstract class GrpcHandlerAdapter extends GrpcHandler {
    @Override
    public final void handleGrpcCall(RpcController controller, InboundMessage request, RpcCallback<OutboundMessage> done) {
        inject();
        ServletResponseAdapter responseAdapter = getResponse(request.getRequestId());
        ResponseEntity<?> response = processProtoRequest(ServletRequestAdapter.wrap(request), responseAdapter);
        response.getHeaders().forEach((s, strings) -> responseAdapter.setHeader(s, strings.get(0)));
        responseAdapter.setStatus(response.getStatusCode().value());
        done.run(responseAdapter.convert());
    }

    protected abstract ResponseEntity<?> processProtoRequest(HttpServletRequest servletRequest, HttpServletResponse servletResponse);

    protected abstract ServletResponseAdapter getResponse(String requestId);

    protected abstract void inject();
}
