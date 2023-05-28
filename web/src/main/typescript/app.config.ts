import { WebSocketServer } from "ws";
import { AuthContext } from "./server/core/auth/auth-context";
import { GrpcConnector } from "./server/core/grpc/grpc-connector";
import { HttpMessageHandler } from "./server/core/http-message-handler";
import * as proto from "./server/core/proto/front-grpc-handler";
import { WebSocketClient } from "./types/socket-client.type";

declare global {
    var grpc: GrpcConnector;
    var grpcService: proto.com.bachlinh.order.core.server.grpc.GrpcHandlerClient;
    var wss: WebSocketServer;
    var clients: Map<string, WebSocketClient>;
    var loggedPool: AuthContext;
    var httpMessageHandler: HttpMessageHandler;
}

export default defineAppConfig({
    authorization: 'Authorization',
    refreshToken: 'Refresh',
    logged: 'logged',
})