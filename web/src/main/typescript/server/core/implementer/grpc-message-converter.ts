import { IncomingMessage, ServerResponse } from "http";
import { InboundMessage } from "../../../types/inbound-message";
import { OutboundMessage } from "../../../types/outbount-message";
import { Converter } from "../converter";

export class GrpcRequestConverter extends Converter<Promise<InboundMessage>, IncomingMessage> {
    async convert(target: IncomingMessage): Promise<InboundMessage> {
        const headers: Array<[string, string]> = JSON.stringify(target.headers)
            .replace(/[{}]/g, '')
            .split(',')
            .map((part: string) => part.split(':'))
            .map((part: Array<string>) => [part[0], part[1]]);
        return {
            body: await target.read(),
            headers: headers,
            method: target.method as string,
            remoteaddress: target.socket.remoteAddress as string,
            url: target.url as string,
            requestid: ''
        };
    }
}

export class ServerResponseConverter extends Converter<ServerResponse, OutboundMessage> {
    private resp: ServerResponse<IncomingMessage>;

    constructor(resp: ServerResponse<IncomingMessage>) {
        super();
        this.resp = resp;
    }

    convert(target: OutboundMessage): ServerResponse<IncomingMessage> {
        this.resp.statusCode = target.status;
        this.resp.setHeader('Content-Type', 'application/json');
        target.headersMap.forEach((value: [string, string]) => {
            if (value[0] === 'Set-Cookie') {
                const cookies = value[1].split(' {cookie} ');
                cookies.forEach((cookie: string) => this.resp.setHeader('Set-Cookie', cookie))
            }
        });
        this.resp.write(target.body);
        return this.resp;
    }
    
}