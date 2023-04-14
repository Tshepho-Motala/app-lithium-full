<template>
  <v-navigation-drawer :value="show" app data-test-id="side-menu">
    <v-list-item>
      <v-list-item-content>
        <v-list-item-title class="title"> LBO SDK </v-list-item-title>
        <v-list-item-subtitle> Registered Plugins </v-list-item-subtitle>
      </v-list-item-content>
    </v-list-item>

    <v-card class="ma-1 pa-1">
      <ClientUser v-if="authenticated" />
      <ClientAuth v-else disableDomain disableForgotPassword dense />
    </v-card>

    <v-list>
      <template v-for="item in sideMenuItems">
        <!-- For no children -->
        <v-list-item
          :key="item.uid"
          link
          :to="item.routeName ? { name: item.routeName } : null"
          :disabled="item.disabled"
          exact
          v-if="!item.hasChildren"
        >
          <v-list-item-title>{{ item.title }}</v-list-item-title>
        </v-list-item>

        <!-- With children -->
        <!-- <v-list-group :key="item.uid" v-model="item.active" no-action v-else>
          <template v-slot:activator>
            <v-list-item-title>{{ item.title }}</v-list-item-title>
          </template>

          <v-list-item
            v-for="child in item.children"
            :key="child.uid"
            link
            :to="child.route ? { name: child.route.name } : null"
            :disabled="child.disabled"
            exact
          >
            <v-list-item-title v-text="child.sideMenu.title"></v-list-item-title>
          </v-list-item>
        </v-list-group> -->
      </template>
    </v-list>

    <!-- <v-list>
      <v-list-group
        v-for="item in sideMenuItems"
        :key="item.uid"
        v-model="item.active"
        :no-action="item.children.legth > 0"
        
      >
        <template v-slot:activator>
          <v-list-item-content>
            <v-list-item-title v-text="item.title"></v-list-item-title>
          </v-list-item-content>
        </template>

        <v-list-item
          v-for="child in item.children"
          :key="child.uid"
        >
          <v-list-item-content>
            <v-list-item-title v-text="child.title"></v-list-item-title>
          </v-list-item-content>
        </v-list-item>
      </v-list-group>
    </v-list> -->

    <!-- <v-list dense>
      <v-list-item-group v-model="sideMenuGroup">
        <v-list-item
          v-for="item in sideMenuItems"
          :key="item.uid"
          link
          :to="item.to ? { name: item.to } : null"
          :href="item.href"
          :disabled="item.disabled"
          exact
        >
          <v-list-item-title>{{ item.title }}</v-list-item-title>

          {{item.children}}
        </v-list-item>
      </v-list-item-group>
    </v-list> -->
  </v-navigation-drawer>
</template>

<script lang='ts'>
import { StoreProxy } from '@/core/store'
import { Component, Inject, Vue } from 'vue-property-decorator'
import ClientAuth from '@/plugin/authentication/ClientAuth.vue'
import ClientUser from '@/plugin/authentication/ClientUser.vue'
import StoreServiceInterface from '@/core/interface/service/StoreServiceInterface'
import AuthenticationStore from '@/plugin/authentication/meta/store'

@Component({
  components: {
    ClientAuth,
    ClientUser
  }
})
export default class SideMenu extends Vue {
  @Inject('storeService') readonly storeService!: StoreServiceInterface

  get show() {
    return StoreProxy.ApplicationStore.showNavigation
  }

  set show(value: boolean) {
    StoreProxy.ApplicationStore.showNavigation = value
  }

  sideMenuGroup = {}
  get sideMenuItems() {
    return StoreProxy.SideMenuStore.items
  }

  store: AuthenticationStore | null = null

  get authenticated(): boolean {
    if (!this.store) {
      return false
    }
    return this.store.authenticated
  }

  mounted() {
    this.store = this.storeService.get(AuthenticationStore)
  }
}
</script>