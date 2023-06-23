import { Store } from "pinia";

export class InputActionCallback {
    constructor(private store: Store, private storePropName: string) {}

    dispatchAction(value: any): void {
        this.store.$patch((state: any) => {
            if (this.storePropName.includes('.')) {
                const props = this.storePropName.split('.');
                const actualState = state[props[0]];
                actualState[props[1]] = value;
                state[props[0]] = actualState;
            } else {
                state[this.storePropName] = value;
            }
        });
    }
}