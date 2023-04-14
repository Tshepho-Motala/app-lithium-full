<template>
  <v-row>
    <v-col cols="12"> Current value in store: {{ storeValue }} </v-col>
    <v-col cols="12">
      <v-btn @click="updateStoreValue">Click to update value</v-btn>
    </v-col>
  </v-row>
</template>

<script lang='ts'>
import StoreServiceInterface from '@/core/interface/service/StoreServiceInterface'
import { Component, Inject, Vue } from 'vue-property-decorator'
import VueStoreDemoStore from './meta/store'

@Component
export default class VueStoreDemo extends Vue {
  // Make sure to inject the store service
  @Inject('storeService') readonly storeService!: StoreServiceInterface

  // Binding directly to a store value is tricky at the best of times,
  // so we use getters (and setters) to proxy
  get storeValue(): boolean {
    const store = this.storeService.get(VueStoreDemoStore)
    return store.works
  }

  mounted() {
    // Register your store.
    // This should be done whenever you need to build the store.
    // You can register the same store multiple times, it will always use one instance.
    this.storeService.add(VueStoreDemoStore)
  }

  updateStoreValue() {
    const store = this.storeService.get(VueStoreDemoStore)
    store.setWorks()

    // We can use store.works = true -- however that is against standard
  }
}
</script>