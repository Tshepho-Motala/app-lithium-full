export default interface RewardProviderGameContract {
    name: string
    providerGameId: string
    guid: string
    description: string | null
    commercialName: string
}

export interface RewardProviderGameListContract extends Array<RewardProviderGameContract>{} 