import { Converter } from '@client/core/converter';
import type { InboundMessage } from '@server/grpc/model/inbound-message';
import type { OutboundMessage } from '@server/grpc/model/outbount-message';
import type { Request, Response } from "express";
import type { ParamsDictionary } from "express-serve-static-core";
import type { IncomingMessage } from 'http';
import type { ParsedQs } from "qs";

export class NodeRequestConveter extends Converter<InboundMessage, Request<ParamsDictionary, any, any, qs.ParsedQs, Record<string, any>>> {
    convert(target: Request<ParamsDictionary, any, any, ParsedQs, Record<string, any>>): InboundMessage {
        const cookies = JSON.stringify(target.cookies).replace(/[{}]/g, '').split(',');
        const headers: Array<[string, string]> = JSON.stringify(target.headers)
            .replace(/[{}]/g, '')
            .split(',')
            .map((part: string) => part.split(':'))
            .map((part: Array<string>) => [part[0], part[1]]);
        headers.push(['Cookie', cookies.join('; ')]);
        return {
            body: target.body,
            headers: headers,
            method: target.method,
            remoteaddress: target.ip,
            url: target.url,
            requestid: ''
        };
    }
}

export class NodeResponseConverter extends Converter<Response<any, Record<string, any>>, OutboundMessage> {
    private res: Response<any, Record<string, any>>;
    
    constructor(res: Response<any, Record<string, any>>) {
        super();
        this.res = res;
    }

    convert(target: OutboundMessage): Response<any, Record<string, any>> {
        this.res.statusCode = target.status;
        this.res.contentType('application/json');
        target.headersMap.forEach((value: [string, string]) => {
            if (value[0] === 'Set-Cookie') {
                const cookies = value[1].split(' {cookie} ');
                cookies.forEach((cookie: string) => this.res.header('Set-Cookie', cookie))
            }
        });
        return this.res.send(target.body);
    }
}

export class IncomingMessageConverter extends Converter<InboundMessage, IncomingMessage> {
    convert(target: IncomingMessage): InboundMessage {
        const cookies = JSON.stringify(target.headers.cookie).replace(/[{}]/g, '').split(',');
        const headers: Array<[string, string]> = JSON.stringify(target.headers)
            .replace(/[{}]/g, '')
            .split(',')
            .map((part: string) => part.split(':'))
            .map((part: Array<string>) => [part[0], part[1]]);
        headers.push(['Cookie', cookies.join('; ')]);
        let data = '';
        target.on('data', (chunk) => {
            data += chunk;
        })
        target.on('end', () => data = JSON.stringify(data));
        return {
            body: data,
            headers: headers,
            method: target.method as string,
            remoteaddress: target.socket.remoteAddress as string,
            url: target.url as string,
            requestid: ''
        };
    }
    
}