import { ProxyWatchers, VuexModule } from 'vuex-class-component/dist/interfaces'

export default interface StoreServiceInterface {
  /**
   * Adds a new store to VueX and the StoreProxy
   * @param cls Store to add
   */
  add<T extends typeof VuexModule>(cls: T): ProxyWatchers & InstanceType<T>

  /**
   * Fetches an existing store from VueX
   * @param cls Store to get
   */
  get<T extends typeof VuexModule>(cls: T): ProxyWatchers & InstanceType<T>
}
