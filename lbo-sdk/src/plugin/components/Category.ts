import ProviderCategoryContract from '@/core/interface/contract-interfaces/service-promo/ProviderCategoryContract'
import { nanoid } from 'nanoid'
import { DomainItemInterface } from '../cms/models/DomainItem'

export default class Category {
  id: string = nanoid()

  constructor(public name: string, public domain: DomainItemInterface) {}

  static fromContract(contract: ProviderCategoryContract, domain: DomainItemInterface): Category {
    return new this(contract.name, domain)
  }

  toContract(): ProviderCategoryContract {
    return {
      name: this.name
    }
  }
}
