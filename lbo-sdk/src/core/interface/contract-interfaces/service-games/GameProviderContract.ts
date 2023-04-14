import { DomainItemInterface } from '@/plugin/cms/models/DomainItem'

export default interface GameProviderContract {
  id: number
  deleted: boolean
  domain: DomainItemInterface
  name: string
  playersOnline: number | null
  version: number
}
