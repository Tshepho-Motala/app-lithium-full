import { RouterInterface } from './RouterInterface'
import { SideMenuItemInterface } from './SidebarInterface'

export interface PluginRegistryInterface {
  plugins: PluginRegistrarInterface[]
  getByUid(uid: string): PluginRegistrarInterface | false
}

export interface PluginRegistrarInterface {
  importer: () => Promise<any> | null
  uid: string
  route?: RouterInterface | undefined
  sideMenu?: SideMenuItemInterface | undefined
  // position?: ModulePositionInterface | undefined
}

export interface ModulePositionInterface {
  parentName: string
}
