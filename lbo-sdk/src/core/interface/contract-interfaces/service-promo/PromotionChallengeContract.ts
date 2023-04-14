import { ExtraFieldValueListContract } from './ExtraFieldContract'

export interface PromotionChallengeContract {
  sequenceNumber: number
  description: string

  /**
   * If TRUE then all rules need to be completed to 100% to activate
   * If FALSE then all rules will add up to 100%
   * 
   * eg: https://drive.google.com/file/d/1_Bi1rzmQVw68uvkpN8AQsCM5-2qUC1DD/view (2:40 - 3:40)
   * 
   * @default false
   */
  requiresAllRules: boolean
  rules: PromotionChallengeRuleContract[]
}

export interface PromotionChallengeProviderContract {
  url: string
  category: string
}

export interface PromotionChallengeRuleContract {
  promoProvider: PromotionChallengeProviderContract | null
  activity: {
    name: string
  }
  operation: string
  value: number | null // This can only be a number or null
  activityExtraFieldRuleValues: ExtraFieldValueListContract
}
