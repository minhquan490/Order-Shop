import * as grpc from '@grpc/grpc-js';
import { ServiceClientConstructor } from '@grpc/grpc-js';
import * as protoLoader from '@grpc/proto-loader';

export class GrpcConnector {
    private readonly serverUrl = process.env.SERVER_GRPC;
    private grpcObject: ServiceClientConstructor;

    constructor() {
        const packageDefinition = protoLoader.loadSync(
            __dirname + '../../../../../../../../common/core/src/main/proto/front-grpc-handler.proto',
            {
                keepCase: true,
                longs: String,
                enums: String,
                defaults: true,
                oneofs: true,
                objects: true
            }
        );
        const object = grpc.loadPackageDefinition(packageDefinition);
        //@ts-ignore
        this.grpcObject = object.com['bachlinh'].order.core.server.grpc.GrpcHandler;
    }

    connect(): Function {
        return new this.grpcObject(
            this.serverUrl as string,
            grpc.ChannelCredentials.createInsecure()
        ).handleGrpcCall;
    }
}