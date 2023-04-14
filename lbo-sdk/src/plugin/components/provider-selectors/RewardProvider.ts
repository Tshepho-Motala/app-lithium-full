import RewardProviderContract from '@/core/interface/contract-interfaces/service-reward/RewardProviderContract'
import { nanoid } from 'nanoid'
import { DomainItemInterface } from '../../cms/models/DomainItem'

export default class RewardProvider {
  id = nanoid()

  constructor(public name: string, public url: string) {}

  static fromContract(contract: RewardProviderContract, domain: DomainItemInterface): RewardProvider {
    return new this(contract.name, contract.url)
  }

  toContract(): RewardProviderContract {
    return {
      name: this.name,
      url: this.url
    }
  }
}
