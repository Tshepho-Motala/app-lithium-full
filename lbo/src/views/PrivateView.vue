<template>
  <v-app>
    <v-app-bar app>
      <v-app-bar-nav-icon @click.stop="drawer = !drawer"></v-app-bar-nav-icon>

      <span>Lithium Back Office Vue POC</span>

      <v-spacer></v-spacer>

      <v-menu bottom left offset-y :close-on-content-click="false">
        <template v-slot:activator="{ on, attrs }">
          <v-btn class="ma-5" v-bind="attrs" v-on="on" color="info" text>
            <v-icon>mdi-cog-outline</v-icon>
          </v-btn>
        </template>

        <div style="background: white">
          <v-list>
            <v-list-item v-if="isAdmin" :to="{ name: 'admin' }">
              <v-list-item-icon>
                <v-icon>mdi-wrench-outline</v-icon>
              </v-list-item-icon>
              <v-list-item-content>
                <v-list-item-title>Admin</v-list-item-title>
              </v-list-item-content>
            </v-list-item>

            <v-list-item v-if="authenticated" @click="logout">
              <v-list-item-icon>
                <v-icon>mdi-logout-variant</v-icon>
              </v-list-item-icon>
              <v-list-item-content>
                <v-list-item-title>Logout</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item v-else :to="{ name: 'login' }">
              <v-list-item-icon>
                <v-icon>mdi-login-variant</v-icon>
              </v-list-item-icon>
              <v-list-item-content>
                <v-list-item-title>Login</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-list>
        </div>
      </v-menu>
    </v-app-bar>

    <v-navigation-drawer v-model="drawer" app permanent>
      <SideMenuList />
    </v-navigation-drawer>

    <v-main>
      <v-container>
        <span class="text-title">Private View</span>
      <router-view></router-view>
      </v-container>
    </v-main>
  </v-app>
</template>

<script lang='ts'>
import { StoreProxy } from '@/store'
import { Vue, Component } from 'vue-property-decorator'
import { Role } from '@/common/enums'

import SideMenuList from '@/components/SideMenuList.vue'

@Component({ components: { SideMenuList } })
export default class PublicView extends Vue {
  drawer = true

  get isAdmin(): boolean {
    if (!this.authenticated) {
      return false
    }
    return this.role === Role.Administrator
  }

  get role(): Role {
    return StoreProxy.AuthStore.role
  }

  get authenticated(): boolean {
    return StoreProxy.AuthStore.authenticated
  }

  async logout() {
    await StoreProxy.AuthStore.logout()
    this.$router.push({ name: 'login' })
  }
}
</script>

<style scoped lang="scss">
</style>
