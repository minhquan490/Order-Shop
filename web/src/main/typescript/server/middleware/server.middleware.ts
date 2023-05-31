import { WebSocketServer } from "ws";
import { GrpcConnector } from "../core/grpc/grpc-connector";
import { GrpcHttpMessageHandler } from "../core/grpc/grpc-http-message-handler";
import { LoggedPool } from "../core/implementer/logged-pool";

export default defineEventHandler((event) => {
    if (!globalThis.grpc) {
        globalThis.grpc = new GrpcConnector();
    }
    if (!globalThis.wss) {
        //@ts-ignore
        globalThis.wss = new WebSocketServer({server: event.node.res.socket?.server});
    }
    if (!globalThis.loggedPool) {
        globalThis.loggedPool = new LoggedPool();
    }
    if (!globalThis.grpcService) {
        globalThis.grpcService = globalThis.grpc.connect();
    }
    if (!globalThis.httpMessageHandler) {
        globalThis.httpMessageHandler = new GrpcHttpMessageHandler(globalThis.grpcService);
    }
})