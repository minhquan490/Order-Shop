import { randomUUID } from "crypto";
import { WebSocketClient } from "../../types/socket-client.type";

export default defineEventHandler((handler) => {

    wss.on('connection', (socket) => {
        socket.on('upgrade', (request) => {
            // TODO implement logic authorization
            socket.close();
        });

        socket.on('open', () => {
            const randomId = randomUUID({disableEntropyCache: true});
            const client: WebSocketClient = {
                id: randomId,
                readyState: socket.readyState,
                send: (data: string) => socket.send(data),
                isAdmin: false,
                actual: socket
            };
            clients.set(randomId, client);
        });
        socket.on('close', () => {
            let id;
            const entries = clients.entries();
            for (const entry of entries) {
                if (entry['1'].actual === socket) {
                    id = entry['0'];
                    break;
                }
            }
            if (id) {
                clients.delete(id);
            }
        })
    });
})
