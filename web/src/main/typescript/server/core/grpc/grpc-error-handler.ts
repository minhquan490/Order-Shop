import { IncomingMessage, ServerResponse } from "http";
import { GrpcStatus } from "../../../types/grpc-status.type";
import { HttpErrorHandler } from "../http-error-handler";

export class GrpcErrorHandler extends HttpErrorHandler {
    
    applyErrorToResponse(error: Error, response: ServerResponse<IncomingMessage>): void {
        // for apply log
        const status: GrpcStatus = this.messageToStatus(error);
        
        response.statusCode = 503;
        response.write(JSON.stringify({status: 503, messages: ["Service under maintance"]}));
    }
    
    private messageToStatus(error: Error): GrpcStatus {
        const statusString = error.message.split(":")[0];
        const part = statusString.split(" ");
        return {
            code: Number.parseInt(part[0]),
            name: part[1]
        };
    }
}