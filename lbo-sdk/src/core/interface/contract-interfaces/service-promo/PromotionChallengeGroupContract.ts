import { PromotionChallengeContract } from './PromotionChallengeContract'

export interface PromotionChallengeGroupContract {
  sequenced: boolean
  /**
   * @default true
   */
  requiresAllChallenges: boolean
  challenges: PromotionChallengeContract[]
}
