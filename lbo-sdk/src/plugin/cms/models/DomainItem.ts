import { nanoid } from 'nanoid'

export interface DomainItemInterface {
  displayName: string
  name: string
  pd: boolean
  timezone?: string
}
export default class DomainItem implements DomainItemInterface {
  randomId = nanoid()

  constructor(public displayName: string, public name: string, public pd: boolean, public timezone: string = '') { }
}
