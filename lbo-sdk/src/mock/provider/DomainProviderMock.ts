import DomainProviderInterface from '@/core/interface/provider/DomainProviderInterface'
import DomainItem, { DomainItemInterface } from '@/plugin/cms/models/DomainItem'

export default class DomainProviderMock implements DomainProviderInterface {
  getDomains(): Promise<DomainItem[]> {
    // Data taken from $userService.domainsWithAnyRole(['ADMIN'])
    return new Promise((res) => {
      const domains: DomainItemInterface[] = [
        { name: 'livescore', pd: false, displayName: 'Livescore Develop Admin' },
        { name: 'livescore_uk', pd: true, displayName: 'Livescore UK' },

        { name: 'livescore_nigeria', pd: true, displayName: 'Livescore  Nigeria' },
        { name: 'livescore_media', pd: true, displayName: 'Livescore Media' },
        { name: 'livescore_nl', pd: true, displayName: 'Livescore Netherlands' },
        { name: 'livescore_ie', pd: true, displayName: 'Livescore Ireland' },
        { name: 'livescore_za', pd: false, displayName: 'Livescore South Africa' },
        { name: 'virginbet_uk', pd: true, displayName: 'Virginbet UK' },
      ]

      const domainItems: DomainItem[] = []
      for (const item of domains) {
        domainItems.push(new DomainItem(item.displayName, item.name, item.pd))
      }

      setTimeout(() => {
        res(domainItems)
      }, 1000)
    })
  }
}
