import { SocketConnectorService, SocketOperator } from "@services/socket-service";

export class SocketConnector extends SocketConnectorService {
    connect(callback: Function): SocketOperator {
        const sockectUrl = process.env.WEBSOCKET_ENDPOINT;
        if (!sockectUrl) {
            throw new Error('Unknown host');
        }
        const socket = new WebSocket(sockectUrl);
        socket.onmessage = (e: MessageEvent<any>) => callback.call(e.data);
        socket.onerror = () => socket.close();
        socket.onclose = () => setTimeout(() => this.connect(callback), 1000);
        return new InternalSocketOperator(socket);
    }
}

class InternalSocketOperator extends SocketOperator {
    private wrappedSocket: WebSocket;

    constructor(wrappedSocket: WebSocket) {
        super();
        this.wrappedSocket = wrappedSocket;
    }

    send(message: Object): void {
        this.wrappedSocket.send(JSON.stringify(message));
    }

}