import StoreServiceInterface from '@/core/interface/service/StoreServiceInterface'
import { Store } from 'vuex'
import { createProxy, extractVuexModule } from 'vuex-class-component'
import { VuexModule, ProxyWatchers } from 'vuex-class-component/dist/interfaces'

export default class StoreServiceMock implements StoreServiceInterface {
  store: Store<any>
  proxy: { [key: string]: ProxyWatchers & InstanceType<any> }

  constructor(store: Store<any>, proxy: { [key: string]: ProxyWatchers & InstanceType<any> }) {
    this.store = store
    this.proxy = proxy
  }

  add<T extends typeof VuexModule>(cls: T): ProxyWatchers & InstanceType<T> {
    const extracted = extractVuexModule(cls)
    const key = Object.keys(extracted)[0]

    if (cls['__loaded__']) {
      // Don't reload
      return this.proxy[key]
    }
    cls['__loaded__'] = true

    this.store.registerModule(key, extracted[key])

    const proxy = createProxy<T>(this.store, cls)
    this.proxy[key] = proxy

    return proxy
  }

  get<T extends typeof VuexModule>(cls: T): ProxyWatchers & InstanceType<T> {
    const extracted = extractVuexModule(cls)
    const key = Object.keys(extracted)[0]

    return this.proxy[key]
  }
}

(window as any).VueSetStore = (store: Store<any>, proxy: { [key: string]: ProxyWatchers & InstanceType<any> }) => {
  (window as any).vueStore = new StoreServiceMock(store, proxy)
}
