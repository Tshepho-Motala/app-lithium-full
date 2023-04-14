import { RouterHookInterface, RouterInterface } from '@/common/interfaces'
import { VueRouter } from 'vue-router/types/router'
import PluginHost from '@/components/PluginHost.vue'

export default class RouterHook implements RouterHookInterface {
  router: VueRouter

  constructor(router: VueRouter) {
    this.router = router
  }

  // async add(uid: string, { name, path, parentName, meta, redirect }: RouterInterface) {
  //   const model = new RouteModel(name, path, { parentName, meta, redirect })
  //   this.router.addRoute({
  //     ...model.toRouteConfig(),
  //     props: {
  //       uid
  //     }
  //   })

  //   return name
  // }

  async add(
    uid: string,
    { name, path, parentName, meta, redirect }: RouterInterface
  ) {
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
      this.router.addRoute('private', {
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
