import RewardContract, {
  RewardComponentGameContract,
  RewardComponentValueContract
} from '@/core/interface/contract-interfaces/service-reward/RewardContract'
import { DomainItemInterface } from '@/plugin/cms/models/DomainItem'
import { nanoid } from 'nanoid'
import RewardType from './RewardType'

export default class Reward {
  id: string = nanoid()

  get completed() {
    return this.name && this.domain && this.types.length > 0 && this.code && this.description
  }

  constructor(
    public name: string,
    public types: RewardType[],
    public code: string,
    public description: string,
    public enabled: boolean,
    public domain: DomainItemInterface,
    public lifetimeValue: string,
    public lifetimeGranularity: string
  ) {}

  toContract(): RewardContract {
    const rewardTypes = this.types.map((t) => t.toComponent())

    return {
      name: this.name,
      code: this.code,
      description: this.description,
      domainName: this.domain.name,
      validFor: parseInt(this.lifetimeValue),
      validForGranularity: parseInt(this.lifetimeGranularity),
      rewardTypes
    }
  }
}
