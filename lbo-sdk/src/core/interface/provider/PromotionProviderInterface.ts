import { Promotion } from '@/plugin/promotions/Promotion'

export default interface PromotionProviderInterface {
  get(): Promise<Promotion[]>
  getById(id: string): Promise<Promotion | null>
  getPromotionsBetween(start: Date, end: Date): Promise<Promotion[]>

  add(promotion: Promotion): Promise<void>
  update(id: string, promotion: Promotion): Promise<void>
}
