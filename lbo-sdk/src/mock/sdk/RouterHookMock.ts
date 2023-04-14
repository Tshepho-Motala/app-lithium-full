import { RouterHookInterface, RouterInterface } from '@/core/interface/sdk/RouterInterface'
import { VueRouter } from 'vue-router/types/router'

import PluginHost from '@/core/components/PluginHost.vue'

export default class RouterHookMock implements RouterHookInterface {
  router: VueRouter

  constructor(router: VueRouter) {
    this.router = router
  }

  /**
   * DEPRECATED!! Use Routes instead
   * @param uid UID
   * @param param1 PARAMS
   */
  async add(uid: string, { name, path, parentName, meta, redirect }: RouterInterface) {
    if (parentName) {
      this.router.addRoute(parentName, {
        name,
        path,
        meta,
        redirect,
        component: PluginHost,
        props: {
          uid
        }
      })
    } else {
      this.router.addRoute({
        name,
        path,
        meta,
        redirect,
        component: PluginHost,
        props: {
          uid
        }
      })
    }
  }
}
