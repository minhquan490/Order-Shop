export type OutboundMessage = {
    status: number;
    headersMap: Array<[string, string]>;
    body: Uint8Array | string;
};
