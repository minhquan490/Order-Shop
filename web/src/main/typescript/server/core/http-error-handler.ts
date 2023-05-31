import { IncomingMessage, ServerResponse } from "http";

export abstract class HttpErrorHandler {
    abstract applyErrorToResponse(error: Error, response: ServerResponse<IncomingMessage>): void;
}