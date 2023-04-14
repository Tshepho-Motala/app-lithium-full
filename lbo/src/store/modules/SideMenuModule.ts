import { SideMenuItemInterface } from '@/common/interfaces'
import apiConnect from '@/modules/apiConnect'
import { action, createModule, mutation } from 'vuex-class-component'

const SideMenuModule = createModule({
  namespaced: 'SideMenuModule',
  strict: false,
  target: 'nuxt'
})

export class SideMenuStore extends SideMenuModule {
  get items(): SideMenuItemInterface[] {
    return [...this.coreItems, ...this.pluginItems]
  }

  coreItems: SideMenuItemInterface[] = []
  pluginItems: SideMenuItemInterface[] = []

  loading = false

  @mutation
  addMenuItem(item: SideMenuItemInterface) {
    this.coreItems.push(item)
  }

  @mutation
  removeMenuItem(item: SideMenuItemInterface) {
    const coreIndex = this.coreItems.findIndex((x) => x.uid === item.uid)
    const pluginIndex = this.pluginItems.findIndex((x) => x.uid === item.uid)
    if (coreIndex > -1) {
      this.coreItems.splice(coreIndex, 1)
    }
    if (pluginIndex > 1) {
      this.pluginItems.splice(pluginIndex, 1)
    }
  }

  @action
  async fetchMenuItems() {
    this.loading = true
    setTimeout(async () => {
      const menuItems = await apiConnect.config.sideMenuItems()
      this.coreItems.push(...menuItems)
      this.loading = false
    }, 3000)
  }
}
