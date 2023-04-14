import { Challenge } from '@/plugin/promotions/challenge/Challenge'

export interface ChallengeProviderInterface {
  getAll(): Promise<Challenge[]>
  getByProvider(providerId: string): Promise<Challenge[]>
  getByPromotion(promotionId: string): Promise<Challenge[]>
  getById(id: string): Promise<Challenge | null>
  addChallenge(challenge: Challenge): Promise<void>
  updateChallenge(id: string, challenge: Challenge): Promise<void>
}
