import { IncomingMessage, ServerResponse } from "http";
import { promisify } from "util";
import { Converter } from "../converter";
import { HttpErrorHandler } from "../http-error-handler";
import { HttpMessageHandler } from "../http-message-handler";
import { GrpcRequestConverter, ServerResponseConverter } from "../implementer/grpc-message-converter";
import * as proto from "../proto/front-grpc-handler";
import { GrpcErrorHandler } from "./grpc-error-handler";

export class GrpcHttpMessageHandler extends HttpMessageHandler {
    private requestConverter: Converter<Promise<proto.com.bachlinh.order.core.server.grpc.InboundMessage>, IncomingMessage>;
    private grpcService: proto.com.bachlinh.order.core.server.grpc.GrpcHandlerClient;
    private errorHandler: HttpErrorHandler;

    constructor(grpcService: proto.com.bachlinh.order.core.server.grpc.GrpcHandlerClient) {
        super();
        this.requestConverter = new GrpcRequestConverter();
        this.grpcService = grpcService;
        this.errorHandler = new GrpcErrorHandler();
    }

    override async handle(request: IncomingMessage, response: ServerResponse<IncomingMessage>): Promise<ServerResponse<IncomingMessage> | string> {
        const responseConverter = new ServerResponseConverter(response);
        
        const req = await this.requestConverter.convert(request);
        const data = await this.processRequest(this.grpcService, req);
        try {
            if (typeof data !== 'undefined') {
                grpcService.getChannel().close();
                return responseConverter.convert(data);
            } else {
                return response;
            }
        } catch (e) {
            const error: Error = this.assignError(e);
            this.errorHandler.applyErrorToResponse(error, response);
            return response;
        }
    }

    private async processRequest(grpcService: proto.com.bachlinh.order.core.server.grpc.GrpcHandlerClient, req: proto.com.bachlinh.order.core.server.grpc.InboundMessage) {
        return promisify(grpcService.handleGrpcCall.bind(grpcService))(req);
    }

    private assignError(error: unknown): Error {
        //@ts-ignore
        const message = `${error.message}`;
        return Object.assign(new Error(message), error);
    }
}