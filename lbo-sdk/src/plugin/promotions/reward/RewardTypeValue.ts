import { RewardComponentValueContract } from '@/core/interface/contract-interfaces/service-reward/RewardContract'

export default class RewardTypeValue {
  constructor(
    public url: string,
    public instant: boolean,
    public notificationMessage: string,
    public rewardTypeName: string,
    public rewardTypeFieldName: string,
    public value: string
  ) {}

  toComponentContract(): RewardComponentValueContract {
    return {
      rewardTypeFieldName: this.rewardTypeFieldName,
      value: this.value
    }
  }
}
