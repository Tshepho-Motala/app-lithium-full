import { PluginRegistrarInterface } from './PluginInterface'

export interface SidebarHookInterface {
  items: SideMenuItemInterface[]

  add(uid: string, item: SideMenuItemInterface): void
  remove(item: SideMenuItemInterface): void
}

export interface SideMenuItemInterface {
  uid?: string | undefined
  // Display
  title: string
  subtitle?: string | null
  icon?: string | null

  // Location
  routeName?: string | null
  href?: string | null

  // Functional
  disabled?: boolean

  readonly hasChildren: boolean
  children: PluginRegistrarInterface[]

  addChildPlugin(plugin: PluginRegistrarInterface, title: string): void
}
