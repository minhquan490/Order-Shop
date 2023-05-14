import * as grpc from '@grpc/grpc-js';
import * as protoLoader from '@grpc/proto-loader';

export class GrpcConnector {
    private readonly serverUrl = process.env.SERVER_GRPC;
    private grpcObject: grpc.ServiceClientConstructor;

    constructor() {
        const packageDefinition = protoLoader.loadSync(
            __dirname + '../../../../../../../common/core/src/main/proto/front-grpc-handler.proto',
            {
                keepCase: true,
                longs: String,
                enums: String,
                defaults: true,
                oneofs: true
            }
        );
        this.grpcObject = grpc.loadPackageDefinition(packageDefinition)
            .grpchandler as grpc.ServiceClientConstructor;
    }

    connect(): Function {
        console.log(this.grpcObject);
        return new this.grpcObject(
            this.serverUrl as string,
            grpc.ChannelCredentials.createInsecure()
        ).handleGrpcCall;
    }
}
