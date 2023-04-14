<template>
  <div v-if="loaded">
    <component :is="dynamicComponent"></component>
  </div>
</template>

<script lang="ts">
import { PluginRegistrarInterface } from '@/common/interfaces'
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
    this.dynamicComponent = await this.module.importer()

    this.loaded = true
  }
}
</script>

<style>
</style>