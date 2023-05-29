import * as grpc from '@grpc/grpc-js';
import * as proto from "../proto/front-grpc-handler";

export class GrpcConnector {
    private readonly serverUrl = process.env.SERVER_GRPC;

    constructor() {}

    connect(): proto.com.bachlinh.order.core.server.grpc.GrpcHandlerClient {
        const url = this.serverUrl as string;
        console.log(`Server url: ${url}`);
        return new proto.com.bachlinh.order.core.server.grpc.GrpcHandlerClient(url, 
            grpc.ChannelCredentials.createInsecure(), 
            {
                "grpc.keepalive_time_ms": 50000,
                "grpc.enable_retries": 10,
                "grpc.max_reconnect_backoff_ms": 100,
                "grpc.max_concurrent_streams": 10000,
                "grpc.keepalive_permit_without_calls": 50000
            },
        );
    }
}