import GameProviderContract from '@/core/interface/contract-interfaces/service-games/GameProviderContract'
import { DomainItemInterface } from '@/plugin/cms/models/DomainItem'
import AxiosApiClient from '../../AxiosApiClient'

export default class ServiceGamesClient extends AxiosApiClient {
  localPrefix: string = 'service-games/backoffice/'
  livePrefix: string = 'services/service-games/backoffice/'

  getGameSuppliersByDomain(domain: DomainItemInterface): Promise<GameProviderContract | null> {
    return this.get(domain.name, 'game-suppliers', 'find-by-domain')
  }
}
