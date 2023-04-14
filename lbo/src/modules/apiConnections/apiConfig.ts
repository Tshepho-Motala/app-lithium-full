import { Config } from '@/common/config'
import { ConfigEndpoint, Role } from '@/common/enums'
import httpCommon from '@/common/httpCommon'
import SideMenuItemModel from '@/common/models/MenuModel'
import RouteModel from '@/common/models/RouteModel'
import { StoreProxy } from '@/store'
import mockRoutes from '../apiMock/mockRoutes'
import mockSideMenuItems from '../apiMock/mockSideMenuItems'

export default class ApiConfig {
  url = process.env['API_URL'] || Config.ApiUrl
  apiAvailable = false

  /**
   * Fetches a list of Side Menu Items based off the current user's Role
   * @returns Side Menu Items
   */
  async sideMenuItems(): Promise<SideMenuItemModel[]> {
    if (!this.apiAvailable) {
      return await this.mockSideMenuItemsByRole()
    }

    const headers = await httpCommon.getHeaders(
      new Map([['X-Role', StoreProxy.AuthStore.role.toString()]])
    )

    const response = await fetch(
      `${this.url}${ConfigEndpoint.sideMenuItemsGet}`,
      {
        credentials: 'include',
        headers
      }
    )
    return (await response.json()) as SideMenuItemModel[]
  }

  /**
   * Fetches a list of Side Menu Items based off the current user's Role
   * @returns Side Menu Items
   */
  async routerItems(): Promise<RouteModel[]> {
    if (!this.apiAvailable) {
      return await this.mockRouteByRole()
    }

    const headers = await httpCommon.getHeaders(
      new Map([['X-Role', StoreProxy.AuthStore.role.toString()]])
    )

    const response = await fetch(
      `${this.url}${ConfigEndpoint.routerItemsGet}`,
      {
        credentials: 'include',
        headers
      }
    )
    return (await response.json()) as RouteModel[]
  }

  /**
   * Fetches Mock Side Menu Items when no API is available
   * @returns Mock Side Menu Items
   */
  private mockSideMenuItemsByRole(): Promise<SideMenuItemModel[]> {
    if (StoreProxy.AuthStore.role === Role.Administrator) {
      return Promise.resolve(mockSideMenuItems.admin)
    }
    return Promise.resolve(mockSideMenuItems.user)
  }

  /**
   * Fetches Mock Routes when no API is available
   * @returns Mock Routes
   */
  private mockRouteByRole(): Promise<RouteModel[]> {
    if (StoreProxy.AuthStore.role === Role.Administrator) {
      return Promise.resolve(mockRoutes.admin)
    }
    return Promise.resolve(mockRoutes.user)
  }
}
