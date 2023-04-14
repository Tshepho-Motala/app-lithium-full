import { SideMenuItemInterface } from '@/core/interface/sdk/SidebarInterface'
import { SideMenuItem } from '@/core/components/side-menu/SideMenuModel'
import { createModule, mutation } from 'vuex-class-component'

const SideMenuModule = createModule({
  namespaced: 'SideMenuModule',
  strict: false,
  target: 'nuxt'
})

export class SideMenuStore extends SideMenuModule {
  get items(): SideMenuItemInterface[] {
    return [...this.coreItems, ...this.pluginItems]
  }

  coreItems: SideMenuItemInterface[] = [
    // new SideMenuItem('Plugin Registry', 'plugin-registry')
  ]
  pluginItems: SideMenuItemInterface[] = []

  loading = false

  @mutation
  clear() {
    this.coreItems = [
      // new SideMenuItem('Plugin Registry', 'plugin-registry')
    ]
  }

  @mutation
  addMenuItem(item: SideMenuItemInterface) {
    this.coreItems.push(item)
  }

  @mutation
  removeMenuItem(item: SideMenuItemInterface) {
    const coreIndex = this.coreItems.findIndex((x) => x.uid === item.uid)
    const pluginIndex = this.pluginItems.findIndex((x) => x.uid === item.uid)
    if(coreIndex > -1) {
      this.coreItems.splice(coreIndex, 1)
    }
    if(pluginIndex > 1) {
      this.pluginItems.splice(pluginIndex, 1)
    }
  }
}
