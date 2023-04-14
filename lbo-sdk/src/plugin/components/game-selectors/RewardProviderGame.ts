import { RewardComponentGameContract } from '@/core/interface/contract-interfaces/service-reward/RewardContract'
import RewardProviderGameContract from '@/core/interface/contract-interfaces/service-reward/RewardProviderGameContract'

export default class RewardProviderGame {
  constructor(
    public name: string,
    public providerGameId: string,
    public guid: string,
    public description: string | null,
    public commercialName: string
  ) {}

  static fromContract(contract: RewardProviderGameContract): RewardProviderGame {
    return new this(contract.name, contract.providerGameId, contract.guid, contract.description, contract.commercialName)
  }

  toContract(): RewardProviderGameContract {
    return this
  }

  toComponentContract(): RewardComponentGameContract {
    return {
      gameId: this.providerGameId,
      gameName: this.commercialName,
      guid: this.guid
    }
  }
}
