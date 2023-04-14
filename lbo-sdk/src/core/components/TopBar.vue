<template>
  <v-app-bar app data-test-id="app-bar">
    <v-app-bar-nav-icon @click.stop="changeSidebarState"></v-app-bar-nav-icon>

    <v-spacer></v-spacer>

    <v-switch v-model="wide" label="Wide" hide-details></v-switch>
  </v-app-bar>
</template>

<script lang='ts'>
import { StoreProxy } from '@/core/store'
import { Component, Vue } from 'vue-property-decorator'

@Component
export default class TopBar extends Vue {
  get wide() {
    return StoreProxy.ApplicationStore.wide
  }

  set wide(value: boolean) {
    StoreProxy.ApplicationStore.wide = value
  }

  changeSidebarState() {
    StoreProxy.ApplicationStore.changeShowNavigation()
  }
}
</script>