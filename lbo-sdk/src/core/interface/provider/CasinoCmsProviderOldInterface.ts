export default interface CasinoCmsProviderOldInterface {

    lobbyExists(domainName: string);

    add(domainName: string , lobby);

    find(domainName: string, id: number);

    findRevision(domainName: string, id: number, lobbyRevisionId: number);

    modifyLobby(domainName: string, id: number);

    modifyLobbyPost(domainName: string, id: number, lobby);

    modifyAndSaveCurrentLobby(domainName: string, id: number, lobby);
}