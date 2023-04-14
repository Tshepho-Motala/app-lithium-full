<template>
  <div>
    <v-card>
      <v-card-title class="text-h1">
        <div style="width: 100%" class="text-center">Login</div>
      </v-card-title>
      <v-card-subtitle class="text-h5 text-center pt-3">
        Select a Role to login as
      </v-card-subtitle>

      <v-card-text class="pt-6">
        <v-select
          :items="roles"
          v-model="selectedRole"
          item-text="k"
          item-value="v"
        ></v-select>

        <v-divider></v-divider>

        {{ $t('GLOBAL.NUMBEROF_RETRIES_NEXTSERVER_EXCEEDED') }}
      </v-card-text>

      <v-card-actions class="pb-16">
        <div class="mx-auto d-flex contained-container">
          <v-spacer></v-spacer>
          <v-btn color="success" large @click="login">Login</v-btn>
        </div>
      </v-card-actions>
    </v-card>
  </div>
</template>

<script lang='ts'>
import { Role } from '@/common/enums'
import apiConnect from '@/modules/apiConnect'
import { StoreProxy } from '@/store'
import { Component, Inject, Vue } from 'vue-property-decorator'

import PluginManager from '@/plugins/PluginManager'

@Component
export default class LoginPage extends Vue {
  @Inject() readonly pluginManager!: PluginManager

  selectedRole: Role | null = null

  get roles() {
    return [
      // { k: 'Anonymous', v: 0 },
      { k: 'User', v: 1 },
      { k: 'Administrator', v: 2 }
    ]
  }

  async login() {
    if (this.selectedRole === null) {
      return
    }
    await StoreProxy.AuthStore.login(this.selectedRole)
    await this.updateRouter()

    try {
      await this.pluginManager.init()
    } catch {
      console.log('Unable to load external plugins')
    }

    this.$router.push({
      name: 'home'
    })
  }

  async updateRouter() {
    const routerItems = await apiConnect.config.routerItems()

    for (const item of routerItems) {
      if (item.parentName !== null) {
        this.$router.addRoute(item.parentName, item.toRouteConfig())
      } else {
        this.$router.addRoute(item.toRouteConfig())
      }
    }
  }
}
</script>

<style scoped>
</style>
