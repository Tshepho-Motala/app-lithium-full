<template>
  <div>
    <router-view />
    <v-btn @click="login">Login</v-btn>
  </div>
</template>

<script lang="ts">
import {
  Component,
  Provide,
  Vue
} from 'vue-property-decorator'
import { StoreProxy } from './store'

import PluginHost from '@/components/PluginHost.vue'
import { RouterHookInterface, SidebarHookInterface } from './common/interfaces'
import RouterHook from './plugins/hooks/RouterHook'
import SidebarHook from './plugins/hooks/SidebarHook'
import PluginManager from './plugins/PluginManager'
import ApiConnection from './apiConnection'

@Component({
  components: { PluginHost }
})
export default class App extends Vue {
  @Provide() routerHook: RouterHookInterface = new RouterHook(this.$router)
  @Provide() sidebarHook: SidebarHookInterface = new SidebarHook()

  @Provide() pluginManager: PluginManager = new PluginManager(this.$router, this.routerHook, this.sidebarHook)

  drawer = false

  sideMenuGroup = {}
  get sideMenuItems() {
    return StoreProxy.SideMenuStore.items
  }

  login() {
    const apic = new ApiConnection()
    apic.login()
  }
}
</script>

<style>

</style>

<style lang="less">

</style>

<style lang="postcss">

</style>

s