import ActivityContract from './ActivityContract'

export default interface PromotionProviderContract {
  id: number
  name: string
  url: string
  category: string
  activities: ActivityContract[]
}

export interface PromotionProviderListContract extends Array<PromotionProviderContract> {}
