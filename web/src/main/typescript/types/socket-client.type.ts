export type WebSocketClient = {
    id: string,
    send: (message: string) => void,
    readyState: number,
    isAdmin: boolean,
    actual: any,
}