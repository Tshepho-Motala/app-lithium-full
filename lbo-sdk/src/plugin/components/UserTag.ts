import UserTagContract from '@/core/interface/contract-interfaces/service-user/UserTagContract'
import { nanoid } from 'nanoid'
import { DomainItemInterface } from '../cms/models/DomainItem'

export default class UserTag {
  id: string = nanoid()

  constructor(public name: string, public description: string, public domain: DomainItemInterface) {}

  static fromContract(contract: UserTagContract, domain: DomainItemInterface): UserTag {
    const item = new this(contract.name, contract.description, domain)
    item.id = contract.id.toString()
    return item
  }

  toContract(): UserTagContract {
    return { ...this, id: parseInt(this.id) }
  }
}
