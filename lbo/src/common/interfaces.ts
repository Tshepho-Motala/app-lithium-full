import { VueRouter } from 'vue-router/types/router'
import { PagePosition } from './enums'

export interface ModulePositionInterface {
  parentName: string
  position: PagePosition
}

export interface ExternalPluginInterface {
  entry: string
  location: string
}

export interface PluginInterface extends ExternalPluginInterface {
  uid: string
  route?: RouterInterface | undefined
  sideMenu?: SideMenuItemInterface | undefined
  position?: ModulePositionInterface | undefined
}

export interface PluginRegistrarInterface extends PluginInterface {
  importer: () => Promise<any>
}
export interface RouterHookInterface {
  router: VueRouter
  add(uid: string, item: RouterInterface): void
}

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
  to?: string | null
  href?: string | null

  // Functional
  disabled?: boolean
}

export interface RouterInterface {
  path: string
  name: string

  parentName?: string | null
  redirect?: string | { [key: string]: any } | undefined
  meta?: { [key: string]: any } | undefined
}
