export abstract class SocketConnectorService {
  abstract connect(callback: Function): SocketOperator;
}

export abstract class SocketOperator {
  abstract send(message: Object): void;
}