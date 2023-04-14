import RewardContract, { RewardFullDetailsContract } from '@/core/interface/contract-interfaces/service-reward/RewardContract'
import { PlayerRewardHistoryContract } from '@/core/interface/contract-interfaces/service-reward/RewardHistoryContact'
import RewardProviderContract, { RewardProviderListContract } from '@/core/interface/contract-interfaces/service-reward/RewardProviderContract'
import { RewardProviderGameListContract } from '@/core/interface/contract-interfaces/service-reward/RewardProviderGameContract'
import { RewardTypeListContract } from '@/core/interface/contract-interfaces/service-reward/RewardTypeContract'
import { DomainItemInterface } from '@/plugin/cms/models/DomainItem'
import AxiosApiClient from '../../AxiosApiClient'
import { TableContract } from '../generic/TableContract'

export default class ServiceRewardClient extends AxiosApiClient {
  localPrefix: string = 'service-reward/backoffice/'
  livePrefix: string = 'services/service-reward/backoffice/'

  create(domain: DomainItemInterface, reward: RewardContract): Promise<RewardFullDetailsContract | null> {
    return this.postJson(reward, domain.name, 'rewards')
  }

  getRewardById(domain: DomainItemInterface, rewardId: string): Promise<RewardFullDetailsContract | null> {
    return this.get(domain.name, 'rewards', rewardId)
  }

  getProviders(domain: DomainItemInterface): Promise<RewardProviderListContract | null> {
    return this.get(domain.name, 'providers')
  }

  getProviderGames(domain: DomainItemInterface, provider: RewardProviderContract): Promise<RewardProviderGameListContract | null> {
    return this.get(domain.name, 'games', provider.url)
  }

  getProviderGamesByRewardType(domain: DomainItemInterface, provider: RewardProviderContract, rewardType: string): Promise<RewardProviderGameListContract | null> {
    return this.get(domain.name, 'games', provider.url, rewardType)
  }

  getRewardTypesForProviders(providers: RewardProviderContract[]): Promise<RewardTypeListContract | null> {
    const body = providers.map((x) => x.url)
    const data = JSON.stringify(body)

    return this.postRaw(data, 'reward-types', 'providers')
  }

  getPlayerRewardHistory(
    domain: DomainItemInterface,
    playerGuid: string,
    start: string,
    length: string,
    args: { [x: string]: string | boolean | number }
  ): Promise<TableContract<PlayerRewardHistoryContract> | null> {
    return this.postWithParameters(
      {
        draw: '0',
        start,
        length,
        playerGuid,
        ...args
      },
      domain.name,
      'rewards',
      'player',
      'all-player'
    )
  }

  getPlayerRewardHistoryOnDomain(
    domain: DomainItemInterface,
    start: string,
    length: string,
    args: { [x: string]: string | boolean | number }
  ): Promise<TableContract<PlayerRewardHistoryContract> | null> {
    return this.postWithParameters<TableContract<PlayerRewardHistoryContract> | null>(
      {
        draw: '0',
        start,
        length,
        ...args
      },
      domain.name,
      'rewards',
      'player',
      'all-domain'
    )
  }

  cancelReward(domain: DomainItemInterface, rewardId: string, playerGuid: string) {
    return this.postWithParameters({ playerGuid }, domain.name, 'rewards', 'player', rewardId, 'cancel-reward')
  }

  cancelRewardComponentForPlayer(domain: DomainItemInterface, playerRewardTypeHistoryId: string, playerGuid: string) {
    return this.postWithParameters(
      {
        playerGuid
      },
      domain.name,
      'rewards',
      'player',
      playerRewardTypeHistoryId,
      'cancel-reward-type'
    )
  }
}
