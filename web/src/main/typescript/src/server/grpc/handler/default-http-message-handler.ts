import type { Converter } from "@client/core/converter";
import { HttpMessageHandler } from "@server/grpc/handler/http-message-handler";
import type { InboundMessage } from "@server/grpc/model/inbound-message";
import type { OutboundMessage } from "@server/grpc/model/outbount-message";
import { NodeRequestConveter, NodeResponseConverter } from "@server/grpc/node-message-converter";
import type { Request, Response } from "express";
import type { ParamsDictionary } from "express-serve-static-core";
import type { ParsedQs } from "qs";

export class DefaultHttpMessageHandler extends HttpMessageHandler {
    private requestConverter: Converter<InboundMessage, Request<ParamsDictionary, any, any, qs.ParsedQs, Record<string, any>>>;
    private responseConverter?: Converter<Response<any, Record<string, any>>, OutboundMessage>;

    constructor(private grpcRemoteMethod: Function) {
        super();
        this.requestConverter = new NodeRequestConveter();
    }
    
    handle(request: Request<ParamsDictionary, any, any, ParsedQs, Record<string, any>>, response: Response<any, Record<string, any>>): void {
        this.setResponseConverter(new NodeResponseConverter(response));
        const grpcResponse: OutboundMessage = this.grpcRemoteMethod.call(this.requestConverter.convert(request));
        const res = this.responseConverter?.convert(grpcResponse);
        res?.end();
    }
    
    setResponseConverter(responseConverter: Converter<Response<any, Record<string, any>>, OutboundMessage>): void {
        this.responseConverter = responseConverter;
    }
}