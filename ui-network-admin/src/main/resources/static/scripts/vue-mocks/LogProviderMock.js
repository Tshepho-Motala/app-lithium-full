class LogProviderMock {
    constructor() {
        this.logLevel = 2;
    }
    get preventLog() {
        return this.logLevel === 0;
    }
    get noError() {
        return this.preventLog || this.logLevel < 1;
    }
    get noWarn() {
        return this.preventLog || this.logLevel < 2;
    }
    get noLog() {
        return this.preventLog || this.logLevel < 3;
    }
    get currentLogLevel() {
        return this.logLevel;
    }
    setLogLevel(logLevel) {
        this.logLevel = logLevel;
    }
    log(...data) {
        if (this.noLog) {
            return;
        }
        console.log(...data);
    }
    warn(...data) {
        if (this.noWarn) {
            return;
        }
        console.warn(...data);
    }
    error(...data) {
        if (this.noError) {
            return;
        }
        console.error(...data);
    }
}

window.vueLog = new LogProviderMock()