export type InboundMessage = {
    url: string;
    method: string;
    headers: Array<[string, string]>;
    remoteaddress: string;
    body: Uint8Array | string;
    requestid: string;
};
