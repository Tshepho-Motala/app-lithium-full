<template>
  <v-app data-test-id="vue-app">
    <TopBar />

    <SideMenu />

    <v-main data-test-id="vue-main">
      <v-container :fluid="fluidContainer">
        <router-view></router-view>
      </v-container>
    </v-main>

    <!-- GLOBAL DIALOGS -->
    <GenericDialog />
    <ConfirmDialog />
  </v-app>
</template>

<script lang="ts">
import { Component, Provide, ProvideReactive, Vue } from 'vue-property-decorator'
import { PluginRegistryInterface, PluginRegistrarInterface } from '@/core/interface/sdk/PluginInterface'
import { SidebarHookInterface } from '@/core/interface/sdk/SidebarInterface'
import { RootScopeInterface } from '@/core/interface/ScopeInterface'
import UserServiceInterface from './core/interface/service/UserServiceInterface'
import TranslateServiceInterface from './core/interface/service/TranslateServiceInterface'
import ListenerServiceInterface from './core/interface/service/ListenerServiceInterface'
import LogServiceInterface from './core/interface/service/LogServiceInterface'
import StoreServiceInterface from './core/interface/service/StoreServiceInterface'

import PluginRegistry from '@/core/PluginRegistry'

import SideMenu from '@/core/components/side-menu/SideMenu.vue'
import TopBar from '@/core/components/TopBar.vue'

import GenericDialog from '@/plugin/components/dialog/GenericDialog.vue'
import ConfirmDialog from '@/plugin/components/dialog/ConfirmDialog.vue'

import { RootScopeMock } from './mock/RootScopeMock'
import RouterHookMock from './mock/sdk/RouterHookMock'
import SidebarHookMock from './mock/sdk/SidebarHookMock'
import UserServiceMock from './mock/service/UserServiceMock'
import TranslateServiceMock from './mock/service/TranslateServiceMock'
import ListenerServiceMock from './mock/service/ListenerServiceMock'
import LogServiceMock from './mock/service/LogServiceMock'
import StoreServiceMock from './mock/service/StoreServiceMock'

import { StoreProxy } from './core/store'
import mockTranslations from '../mock_translations.json'

import routes from './router/routes'
import AxiosApiClient from './core/axios/AxiosApiClient'
import AxiosApiClients from './core/axios/AxiosApiClients'

let translations: Map<string, string> = new Map(Object.entries(mockTranslations))

@Component({
  components: { SideMenu, TopBar, GenericDialog, ConfirmDialog }
})
export default class App extends Vue {
  @Provide() routerHook: RouterHookMock = new RouterHookMock(this.$router)
  @Provide() sidebarHook: SidebarHookInterface = new SidebarHookMock()
  @Provide() rootScope: RootScopeInterface = new RootScopeMock()
  @Provide() userService: UserServiceInterface = new UserServiceMock()
  @Provide() translateService: TranslateServiceInterface = new TranslateServiceMock()
  @Provide() listenerService: ListenerServiceInterface = new ListenerServiceMock()
  @Provide() logService: LogServiceInterface = new LogServiceMock()
  @Provide() storeService: StoreServiceInterface = new StoreServiceMock(this.$store, StoreProxy)

  @Provide() apiClient: AxiosApiClient = new AxiosApiClient(this.userService)
  @Provide() apiClients: AxiosApiClients = new AxiosApiClients(this.userService)

  @ProvideReactive() pluginRegistry!: PluginRegistryInterface

  get fluidContainer() {
    this.logService.log('nw:', StoreProxy.ApplicationStore.wide)
    return StoreProxy.ApplicationStore.wide
  }

  mounted() {
    this.pluginRegistry = new PluginRegistry()

    this.init()
  }

  async init() {
    StoreProxy.SideMenuStore.clear()
    await this.bindAll()
    await this.autoRoutes()
  }

  private async autoRoutes() {
    for (const route of routes) {
      this.$router.addRoute(route)

      const titleOrName = (route.meta ? route.meta.title : route.name) || route.name

      this.sidebarHook.add(route.name, {
        title: titleOrName,
        routeName: route.name,
        hasChildren: false,
        children: [],
        addChildPlugin: () => {}
      })
    }
  }

  // DEPRECATED
  // Will be removed when all plugins have transitioned into routes
  private async bindAll() {
    for (const plugin of this.pluginRegistry.plugins) {
      if (plugin.route) {
        await this.routerHook.add(plugin.uid, plugin.route)
      }
      if (plugin.sideMenu) {
        this.sidebarHook.add(plugin.uid, plugin.sideMenu)
        if (plugin.sideMenu.hasChildren) {
          await this.addRoutesByPlugin(plugin.sideMenu.children)
        }
      }
    }
  }

  private async addRoutesByPlugin(children: PluginRegistrarInterface[]) {
    for (const child of children) {
      if (child.route) {
        this.routerHook.add(child.uid, child.route)
      }
    }
  }
}
</script>

<style>
.red-text {
  color: red;
}
</style>
