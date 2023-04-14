import Vue from 'vue'
import Vuex from 'vuex'
import { extractVuexModule, createProxy } from 'vuex-class-component'

import { AuthStore } from './modules/AuthModule'
import { ProfileStore } from './modules/ProfileModule'
import { SideMenuStore } from './modules/SideMenuModule'

Vue.use(Vuex)

export const store = new Vuex.Store({
  state: {
    lastUpdated: 0
  },
  modules: {
    ...extractVuexModule(AuthStore),
    ...extractVuexModule(ProfileStore),
    ...extractVuexModule(SideMenuStore)
  }
})

export const StoreProxy = {
  AuthStore: createProxy(store, AuthStore),
  ProfileStore: createProxy(store, ProfileStore),
  SideMenuStore: createProxy(store, SideMenuStore)
}
