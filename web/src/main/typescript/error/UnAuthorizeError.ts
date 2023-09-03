export class UnAuthorizeError extends Error {
    private readonly messages: Array<string>;

    constructor(messages: Array<string>) {
        super();
        this.messages = messages;
    }

    public getMessages(): Array<string> {
        return this.messages;
    }
}