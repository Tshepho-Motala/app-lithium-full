import ActivityContract from '@/core/interface/contract-interfaces/service-promo/ActivityContract'
import { nanoid } from 'nanoid'
import { ExtraField } from './PromotionExtraField'

/**
 * @see {{gateway}}/service-promo/backoffice/livescore_uk/provider/list
 */
export class Activity {
  id = nanoid()

  get requiresValue() {
    const forbidValueRequirement = ['registration-success'] /// List of things that do not need a value
    return !forbidValueRequirement.some((x) => x === this.name)
  }

  constructor(public name: string, public promoProviderId: number, public extraFields: ExtraField[], public requiresAllRules: boolean) {}

  static fromContract(contract: ActivityContract): Activity {
    const item = new this(
      contract.name,
      contract.promoProvider,
      contract.extraFields.map((a) => ExtraField.fromContract(a)),
      contract.requiresAllRules
    )
    item.id = contract.id.toString()
    return item
  }

  toContract(): ActivityContract {
    return {
      id: parseInt(this.id),
      name: this.name,
      promoProvider: this.promoProviderId,
      extraFields: this.extraFields.map((e) => e.toContract()),
      requiresValue: this.requiresValue,
      requiresAllRules: this.requiresAllRules
    }
  }
}
