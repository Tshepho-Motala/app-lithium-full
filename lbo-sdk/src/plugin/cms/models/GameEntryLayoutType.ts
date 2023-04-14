export default class GameEntryLayoutType {
    id: number
    type: string

    constructor(code: number, type: string) {
        this.id = code
        this.type = type
    }
}