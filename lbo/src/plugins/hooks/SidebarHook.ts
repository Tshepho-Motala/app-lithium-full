import {
  SidebarHookInterface,
  SideMenuItemInterface
} from '@/common/interfaces'
import { StoreProxy } from '@/store'

export default class SidebarHook implements SidebarHookInterface {
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
