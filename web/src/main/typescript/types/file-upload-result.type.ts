export class UploadFileResult {
    private error: boolean;
    private messages: Array<string>;

    constructor(error: boolean, messages: Array<string>) {
        this.error = error;
        this.messages = messages;
    }

    public get isError() : boolean {
        return this.error;
    }
    
    
    public get getMessages() : Array<string> {
        return this.messages;
    }
}