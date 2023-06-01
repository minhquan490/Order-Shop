import { WebSocketServer } from "ws";
import { LoggedPool } from "../core/implementer/logged-pool";

export default defineEventHandler((event) => {
    if (!globalThis.wss) {
        //@ts-ignore
        globalThis.wss = new WebSocketServer({server: event.node.res.socket?.server});
    }
    if (!globalThis.loggedPool) {
        globalThis.loggedPool = new LoggedPool();
    }
})