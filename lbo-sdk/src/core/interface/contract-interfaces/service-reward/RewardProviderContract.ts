export default interface RewardProviderContract {
  name: string
  url: string
}

export interface RewardProviderListContract extends Array<RewardProviderContract> {}
