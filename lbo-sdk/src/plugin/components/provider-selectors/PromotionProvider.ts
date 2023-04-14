import PromotionProviderContract from '@/core/interface/contract-interfaces/service-promo/PromotionProviderContract'
import { Activity } from '@/plugin/promotions/promotion/PromotionActivity'
import { DomainItemInterface } from '../../cms/models/DomainItem'
import Category from '../Category'

export default class PromotionProvider {
  active = false

  constructor(
    public id: number,
    public name: string,
    public domain: DomainItemInterface,
    public url: string,
    public category: Category,
    public activities: Activity[] = []
  ) {}

  static fromContract(contract: PromotionProviderContract, domain: DomainItemInterface): PromotionProvider {
    const category = new Category(contract.category, domain)
    const activities = contract.activities.map((a) => Activity.fromContract(a))

    return new this(contract.id, contract.name, domain, contract.url, category, activities)
  }

  toContract(): PromotionProviderContract {
    return {
      id: this.id,
      name: this.name,
      url: this.url,
      category: this.category.name,
      activities: this.activities.map((a) => a.toContract())
    }
  }
}
