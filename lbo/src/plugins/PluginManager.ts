import {
  PluginInterface,
  PluginRegistrarInterface,
  RouterHookInterface,
  SidebarHookInterface
} from '@/common/interfaces'
import VueRouter from 'vue-router'

export default class PluginManager {
  pluginServerUrl = 'http://localhost:3000'
  plugins: PluginRegistrarInterface[] = []

  pageModules: { [key: string]: PluginRegistrarInterface[] } = {}

  router: VueRouter
  routerHook: RouterHookInterface
  sidebarHook: SidebarHookInterface

  get uids() {
    return this.plugins.filter((x) => x.route).map((x) => x.uid)
  }

  constructor(
    router: VueRouter,
    routerHook: RouterHookInterface,
    sidebarHook: SidebarHookInterface
  ) {
    this.router = router
    this.routerHook = routerHook
    this.sidebarHook = sidebarHook
  }

  public async init() {
    await this.fetchExternalPlugins()
    await this.registerPlugins()
  }

  public getByUid(uid: string): PluginRegistrarInterface | false {
    return this.plugins.find((x) => x.uid === uid) || false
  }

  private async fetchExternalPlugins() {
    const availablePlugins: {
      [key: string]: PluginRegistrarInterface
    } = await fetch(`${this.pluginServerUrl}/plugins.json`).then((x) =>
      x.json()
    )
    const keys = Object.keys(availablePlugins)

    for (const key of keys) {
      const plugin = availablePlugins[key]

      plugin.uid = key
      plugin.importer = this.createImporter(plugin)

      this.plugins.push(plugin)
    }
  }

  private createImporter({
    location,
    entry,
    route
  }: PluginInterface): () => Promise<any> {
    const src = `${this.pluginServerUrl}${location}/${entry}`
    return () => this.createInitiator(src)
  }

  private createInitiator(url: string): Promise<any> {
    const split = url
      .split('/')
      .reverse()[0]
      .match(/^(.*?)\.umd/)
    if (!split) {
      throw new Error("Can't load plugin: " + url)
    }
    const name: string = split[1]

    if ((window as any)[name]) {
      return (window as any)[name]
    }

    const initiator = new Promise((resolve, reject) => {
      const script = document.createElement('script')
      script.async = true
      script.addEventListener('load', () => {
        resolve((window as any)[name])
      })
      script.addEventListener('error', () => {
        reject(new Error(`Error loading ${url}`))
      })
      script.src = url
      document.head.appendChild(script)
    })

    ;(window as any)[name] = initiator

    return initiator
  }

  private async registerPlugins() {
    for (const plugin of this.plugins) {
      if (plugin.route) {
        this.routerHook.add(plugin.uid, plugin.route)
      }
      if (plugin.sideMenu) {
        this.sidebarHook.add(plugin.uid, plugin.sideMenu)
      }
      if (plugin.position) {
        if (!this.pageModules[plugin.position.parentName]) {
          this.pageModules[plugin.position.parentName] = []
        }
        this.pageModules[plugin.position.parentName].push(plugin)
      }
    }
  }
}
