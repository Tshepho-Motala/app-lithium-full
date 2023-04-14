<template>
 <!-- SHOW A LOADER EXAMPLE WIHT IF -->
 <v-progress-linear indeterminate v-if="loading"></v-progress-linear>
  <v-list nav dense v-else>
    <v-list-item-group v-model="group">
      <v-list-item
        v-for="item in items"
        :key="item.key"
        link
        :to="item.to ? { name: item.to } : null"
        :href="item.href"
        :disabled="item.disabled"
        exact
      >
        <v-list-item-icon>
            <v-icon>{{ item.icon }}</v-icon>
        </v-list-item-icon>
        <v-list-item-title>{{ item.title }}</v-list-item-title>
      </v-list-item>
    </v-list-item-group>
  </v-list>
</template>

<script lang='ts'>
import { StoreProxy } from '@/store'
import { Vue, Component } from 'vue-property-decorator'

@Component
export default class SideMenuList extends Vue {
  group = {}

  get items() {
    return StoreProxy.SideMenuStore.items
  }

  get loading() {
    return StoreProxy.SideMenuStore.loading
  }

  mounted() {
    StoreProxy.SideMenuStore.fetchMenuItems()
  }
}
</script>

<style scoped lang="scss">
</style>
