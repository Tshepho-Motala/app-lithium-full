import Vue from 'vue'
import Vuex from 'vuex'
import { extractVuexModule, createProxy, } from 'vuex-class-component'

import { SideMenuStore } from './modules/SideMenuModule'
import { ApplicationStore } from './modules/ApplicationModule'

Vue.use(Vuex)

export const store = new Vuex.Store({
  state: {
    lastUpdated: 0
  },
  modules: {
    ...extractVuexModule(SideMenuStore),
    ...extractVuexModule(ApplicationStore)
  }
})

export const StoreProxy = {
  SideMenuStore: createProxy(store, SideMenuStore),
  ApplicationStore: createProxy(store, ApplicationStore)
}