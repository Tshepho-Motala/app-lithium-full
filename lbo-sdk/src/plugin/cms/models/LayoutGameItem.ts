export default class LayoutGameItem {
    type = '';
    gameName = '';
    image = '';
    gameID = '';
    badge = '';
    promoId = '';

    constructor(name: string, gameID: string) {
        this.gameName = name;
        this.type = 'game';
        this.gameID = gameID;
    }
}
