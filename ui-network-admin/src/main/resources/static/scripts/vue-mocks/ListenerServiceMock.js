class ListenerServiceMock {
    constructor() {
        this.subscriptions = new Map();
    }
    subscribe(key, fn, replaceExisting) {
        if (this.subscriptions.has(key) && !replaceExisting) {
            console.warn('There is already a subscription with the key: ' + key);
            return;
        }
        this.subscriptions.set(key, fn);
    }
    call(key, params) {
        if (!this.subscriptions.has(key)) {
            console.warn('There is no subscription with the key: ' + key);
            return;
        }
        const promise = this.subscriptions.get(key);
        if (promise !== undefined) {
            if (!params || params.length === 0) {
                promise();
            }
            else {
                promise(params);
            }
        }
    }
}

window.vueListener = new ListenerServiceMock()