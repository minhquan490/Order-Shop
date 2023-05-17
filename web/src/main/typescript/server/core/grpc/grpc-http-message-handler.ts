import { IncomingMessage, ServerResponse } from "http";
import { InboundMessage } from "../../../types/inbound-message";
import { OutboundMessage } from "../../../types/outbount-message";
import { Converter } from "../converter";
import { GrpcRequestConverter, ServerResponseConverter } from "../implementer/grpc-message-converter";
import { GrpcConnector } from "./grpc-connector";
import { HttpMessageHandler } from "./http-message-handler";

export class GrpcHttpMessageHandler extends HttpMessageHandler {
    private requestConverter: Converter<Promise<InboundMessage>, IncomingMessage>;
    private grpcRemoteMethod: Function;

    constructor() {
        super();
        this.requestConverter = new GrpcRequestConverter();
        const connector = new GrpcConnector();
        this.grpcRemoteMethod = connector.connect();
    }

    handle(request: IncomingMessage, response: ServerResponse<IncomingMessage>): void {
        const responseConverter = new ServerResponseConverter(response);
        const grpcResponse: OutboundMessage = this.grpcRemoteMethod.call(this.requestConverter.convert(request));
        responseConverter.convert(grpcResponse).end();
    }
    
}