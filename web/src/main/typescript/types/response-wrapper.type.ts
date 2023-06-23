export class ResponseWrapper<T> {
    constructor(private status: number, private delegate: T | null) {}

    public get getStatus(): number {
        return this.status;
    }
    
    public get getResponse(): T | null {
        return this.delegate;
    }
    
    
    public get isError(): boolean {
        return this.status >= 400;
    }
}