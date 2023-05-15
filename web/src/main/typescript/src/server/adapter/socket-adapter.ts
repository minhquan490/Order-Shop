import type { OutboundMessage } from '@server/grpc/model/outbount-message';
import { IncomingMessageConverter } from '@server/grpc/node-message-converter';
import spdy from 'spdy';
import WebSocket from 'ws';

export class WebSocketAdapter {
    private spdyServer: spdy.server.Server;
    private wss: WebSocket.Server<WebSocket.WebSocket>
    private grpcRemoteMethod?: Function;

    constructor(spdyServer: spdy.server.Server, wss: WebSocket.Server<WebSocket.WebSocket>, grpcRemoteMethod?: Function) {
        this.spdyServer = spdyServer;
        this.wss = wss;
        this.grpcRemoteMethod = grpcRemoteMethod;
    }

    execute(): spdy.server.Server {
        this.spdyServer.on('upgrade', (req, socket, head) => {
            this.wss.handleUpgrade(req, socket, head, (ws) => {
                const authHeader = req.headers.authorization;
                if(authHeader) {
                    if(this.grpcRemoteMethod) {
                        const converter = new IncomingMessageConverter();
                        const message = converter.convert(req);
                        const response: OutboundMessage = this.grpcRemoteMethod.call(message);
                        if (response.status != 200) {
                            ws.close(response.status, response.body.toString());
                        }
                    }
                }
                ws.emit('connection', ws, req);
            });
        });
        return this.spdyServer;
    }
}