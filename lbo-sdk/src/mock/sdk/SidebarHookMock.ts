import { SidebarHookInterface, SideMenuItemInterface } from '@/core/interface/sdk/SidebarInterface'
import { StoreProxy } from '@/core/store'

export default class SidebarHookMock implements SidebarHookInterface {
  get items(): SideMenuItemInterface[] {
    return StoreProxy.SideMenuStore.items
  }

  add(uid: string, item: SideMenuItemInterface): void {
    item.uid = uid
    StoreProxy.SideMenuStore.addMenuItem(item)
  }


  remove(item: SideMenuItemInterface): void {
    StoreProxy.SideMenuStore.removeMenuItem(item)
  }
}
