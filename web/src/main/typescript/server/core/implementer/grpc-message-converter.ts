import { IncomingMessage, ServerResponse } from "http";
import { Converter } from "../converter";
import * as proto from "../proto/front-grpc-handler";

export class GrpcRequestConverter extends Converter<Promise<proto.com.bachlinh.order.core.server.grpc.InboundMessage>, IncomingMessage> {
    private readonly encoder: TextEncoder = new TextEncoder();


    async convert(target: IncomingMessage): Promise<proto.com.bachlinh.order.core.server.grpc.InboundMessage> {
        const headers: Map<string, string> = new Map<string, string>();
        JSON.stringify(target.headers)
            .replace(/[{}]/g, '')
            .split(',')
            .map((part: string) => part.split(':'))
            .forEach(part => headers.set(part[0], part[1]));
        const endpoint = (target.url as string).replace("/api", "");
        const result = new proto.com.bachlinh.order.core.server.grpc.InboundMessage();
        result.body = this.encoder.encode(await target.read());
        result.headers = headers;
        result.method = target.method as string;
        result.remoteAddress = target.socket.remoteAddress as string;
        result.url = endpoint;
        result.requestId = '';
        return result;
    }
}

export class ServerResponseConverter extends Converter<string, proto.com.bachlinh.order.core.server.grpc.OutboundMessage> {
    private readonly decoder: TextDecoder = new TextDecoder();
    private resp: ServerResponse<IncomingMessage>;

    constructor(resp: ServerResponse<IncomingMessage>) {
        super();
        this.resp = resp;
        this.resp.setHeader('Content-Type', 'application/json')
    }

    convert(target: proto.com.bachlinh.order.core.server.grpc.OutboundMessage): string {
        this.resp.statusCode = target.status;
        target.headers.forEach((value, key) => {
            if (key === 'Set-Cookie') {
                const cookies = value.split(' {cookie} ');
                cookies.forEach((cookie: string) => this.resp.setHeader('Set-Cookie', cookie));
            } else {
                this.resp.setHeader(key, value);
            }
        });
        return this.decoder.decode(target.body);
    }
    
}