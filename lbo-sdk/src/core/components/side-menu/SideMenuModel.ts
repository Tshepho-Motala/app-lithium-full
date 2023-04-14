import { PluginRegistrarInterface } from '../../interface/sdk/PluginInterface'
import { SideMenuItemInterface } from '../../interface/sdk/SidebarInterface'

export class SideMenuItem implements SideMenuItemInterface {
  uid?: string | undefined
  title: string
  subtitle?: string | null | undefined
  icon?: string | null | undefined
  routeName?: string | null | undefined
  href?: string | null | undefined
  disabled?: boolean | undefined
  children: PluginRegistrarInterface[]

  get hasChildren() {
    return this.children.length > 0
  }

  constructor(title: string, routeName?: string, children?: PluginRegistrarInterface[]) {
    this.title = title
    this.routeName = routeName
    this.children = children || []
  }

  addChildPlugin(plugin: PluginRegistrarInterface, title: string): void {
    // const child = new SideMenuItem(title, plugin.route?.name)
    // this.addChild(child)
    this.children.push(plugin)
  }
}
