import WebSocket from 'ws';

export abstract class SocketHandler {
    abstract handleIncomeMessage(data: WebSocket.RawData, isBinary: boolean): void;
}