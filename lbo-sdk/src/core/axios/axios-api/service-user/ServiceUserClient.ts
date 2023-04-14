import UserTagContract from '@/core/interface/contract-interfaces/service-user/UserTagContract'
import { DomainItemInterface } from '@/plugin/cms/models/DomainItem'
import AxiosApiClient from '../../AxiosApiClient'

export default class ServiceUserClient extends AxiosApiClient {
  localPrefix: string = 'service-user/backoffice/'
  livePrefix: string = 'services/' + this.localPrefix

  getTagsForDomain(domain: DomainItemInterface): Promise<UserTagContract[] | null> {
    return this.getWithParameter(
      {
        domainNames: domain.name
      },
      'players',
      'tag',
      'list'
    )
  }
}
