import { WebSocketServer } from "ws";
import { LoggedPool } from "../core/implementer/logged-pool";

export default defineEventHandler((event) => {
  if (!globalThis.wss) {
    globalThis.wss = new WebSocketServer({
      //@ts-ignore
      server: event.node.res.socket?.server,
    });
  }
  if (!globalThis.loggedPool) {
    globalThis.loggedPool = new LoggedPool();
  }
  if (process.env.NODE_ENV == "production") {
    const url = event.node.req.url as string;
    const maxage = url.match(/(.+)\.(jpg|jpeg|gif|png|ico|svg|css|js|mjs)/)
      ? 60 * 60 * 12 * 30
      : 60 * 60;
    appendHeader(event, "Cache-Control", `max-age=${maxage},immutable`);
  }
});
