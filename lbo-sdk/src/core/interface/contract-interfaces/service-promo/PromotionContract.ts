import { DomainItemInterface } from '@/plugin/cms/models/DomainItem'
import { PromotionChallengeGroupContract } from './PromotionChallengeGroupContract'

export interface PromotionListContract extends Array<PromotionContract> {}

export default interface PromotionContract {
  /**
   * ID is only needed for editing
   */
  id?: number
  domain: DomainItemInterface
  name: string
  description: string
  xpLevel: number
  /**
   * Format: yyyy-MM-dd
   */
  startDate: string
  /**
   * Format: yyyy-MM-dd
   */
  endDate: string | null
  /**
   * rrule string
   */
  recurrencePattern: string
  redeemableInTotal: number
  redeemableInEvent: number
  eventDuration: number
  eventDurationGranularity: number
  /**
   * @default false
   */
  requiresAllChallengeGroups: boolean
  challengeGroups: PromotionChallengeGroupContract[]
  reward: PromotionRewardReference
  exclusive: boolean
  exclusivePlayers: PromotionExclusivePlayer[]
  userCategories: PromotionUserCategory[]
}

export interface PromotionRewardReference {
  id?: number
  version?: number
  rewardId: number
}

export interface PromotionExclusivePlayer {
  guid: string
}

export interface PromotionUserCategory {
  userCategoryId: number // AKA Tag ID
  type: 'whitelist' | 'blacklist'
}
