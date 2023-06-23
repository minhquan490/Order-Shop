import { Store } from "pinia";

export class TableActionCallback {
    private store: Store;
    private stateName: string;

    constructor(store: Store, stateName: string) {
        this.store = store;
        this.stateName = stateName;
    }

    dispatchAction(value: any): void {
        this.store.$patch((state: any) => state[this.stateName] = value);
    }
}