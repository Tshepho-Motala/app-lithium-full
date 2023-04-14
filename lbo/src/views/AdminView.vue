<template>
  <v-app>
    <v-app-bar app>
      <v-app-bar-nav-icon @click.stop="drawer = !drawer"></v-app-bar-nav-icon>

      <span>LBO Admin Zone</span>

      <v-spacer></v-spacer>
    </v-app-bar>

    <v-navigation-drawer v-model="drawer" app>
      <SideMenuList />
    </v-navigation-drawer>

    <v-main>
      <v-container>
        <span class="text-title">Admin View</span>
        <router-view></router-view>
      </v-container>
    </v-main>
  </v-app>
</template>

<script lang='ts'>
import { Vue, Component } from 'vue-property-decorator'

import SideMenuList from '@/components/SideMenuList.vue'

@Component({ components: { SideMenuList } })
export default class AdminView extends Vue {
  drawer = true
}
</script>

<style scoped>
</style>
