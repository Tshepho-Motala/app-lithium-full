import { SideMenuItemInterface } from '../interfaces'

export default class SideMenuItemModel implements SideMenuItemInterface {
  uid: string | undefined
  title: string
  subtitle: string | null
  icon: string | null
  to: string | null
  href: string | null
  disabled: boolean

  constructor({
    uid,
    title,
    subtitle = null,
    icon = null,
    to = null,
    href = null,
    disabled = false
  }: SideMenuItemInterface) {
    this.uid = uid
    this.title = title
    this.subtitle = subtitle
    this.icon = icon
    this.to = to
    this.href = href
    this.disabled = disabled
  }
}
