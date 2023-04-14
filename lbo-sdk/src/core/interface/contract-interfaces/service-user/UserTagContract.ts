import { DomainItemInterface } from '@/plugin/cms/models/DomainItem'

export default interface UserTagContract {
  name: string
  id: number
  description: string
  domain: DomainItemInterface
}
