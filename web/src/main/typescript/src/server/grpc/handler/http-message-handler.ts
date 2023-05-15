import type { Converter } from '@client/core/converter';
import type { OutboundMessage } from '@server/grpc/model/outbount-message';
import { type Request, type Response } from 'express';
import * as core from 'express-serve-static-core';
import qs from 'qs';

export abstract class HttpMessageHandler {
    abstract handle(request: Request<core.ParamsDictionary, any, any, qs.ParsedQs, Record<string, any>>, response: Response<any, Record<string, any>>): void;
    abstract setResponseConverter(responseConverter: Converter<Response<any, Record<string, any>>, OutboundMessage>): void;
}