import {
  RewardComponentContract,
  RewardComponentGameContract,
  RewardComponentValueContract
} from '@/core/interface/contract-interfaces/service-reward/RewardContract'
import RewardTypeContract, { RewardSetupFieldContract } from '@/core/interface/contract-interfaces/service-reward/RewardTypeContract'
import RewardProviderGame from '@/plugin/components/game-selectors/RewardProviderGame'
import { nanoid } from 'nanoid'
import RewardTypeValue from './RewardTypeValue'

export default class RewardType {
  id: string = nanoid()

  selectedGame: RewardProviderGame[] = []
  fieldValueBinding: RewardTypeValue[] = []
  get instant() {
    return !this.playerAcceptanceRequired
  }

  playerAcceptanceRequired = false
  playerAcceptanceMessage = ''

  get hasFields(): boolean {
    return this.fieldValueBinding.length > 0
  }

  constructor(
    public url: string,
    public name: string,
    public code: null | string,
    public setupFields: RewardSetupFieldContract[],
    public displayGames: boolean
  ) {
    this.setExtraFieldValues()
  }

  private setExtraFieldValues() {
    for (const field of this.setupFields) {
      this.fieldValueBinding.push(new RewardTypeValue(this.url, this.instant, this.playerAcceptanceMessage, this.name, field.name, ''))
    }
  }

  static fromContract(contract: RewardTypeContract): RewardType {
    const item = new this(contract.url, contract.name, contract.code, contract.setupFields, contract.displayGames)
    item.id = contract.id.toString()
    return item
  }

  toContract(): RewardTypeContract {
    return {
      ...this,
      id: parseInt(this.id)
    }
  }

  toComponent(): RewardComponentContract {
    // const rewardTypeGames: RewardComponentGameContract[] = this.selectedGame.map(g => g.toComponentContract())
    const rewardTypeGames: RewardComponentGameContract[] = []
    for (const game of this.selectedGame) {
      const contract = game.toComponentContract()
      rewardTypeGames.push(contract)
    }

    const rewardTypeValues = this.fieldValueBinding.map((f) => f.toComponentContract())

    return {
      url: this.url,
      rewardTypeName: this.name,
      instant: this.instant,
      notificationMessage: this.playerAcceptanceMessage,
      rewardTypeGames,
      rewardTypeValues
    }
  }
}
