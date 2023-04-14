<template>
  <div v-if="loaded">
    <component :is="dynamicComponent"></component>
  </div>
</template>

<script lang="ts">
import { PluginRegistrarInterface } from '@/core/interface/sdk/PluginInterface'
import { Component, Vue, Prop } from 'vue-property-decorator'

@Component
export default class ModuleHost extends Vue {
  @Prop() readonly module!: PluginRegistrarInterface

  loaded = false
  waiting = false
  dynamicComponent: any

  mounted() {
    this.getPluginComponent()
  }

  async getPluginComponent() {
    if (!this.module) {
      return
    }
    this.waiting = false

    const T = await this.module.importer()
    this.dynamicComponent = Vue.extend(T.default)

    this.loaded = true
  }
}
</script>

<style>
</style>