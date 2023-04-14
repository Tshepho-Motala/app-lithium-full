<template>
  <div v-if="loaded">
    <component v-if="showComponent" :is="dynamicComponent"></component>

    <ModuleHost v-for="(module, i) in myModules" :key="`${uid}-module-POSITION-${i}`" :module="module" />
  </div>
</template>

<script lang="ts">
import { PluginRegistrarInterface, PluginRegistryInterface } from '@/core/interface/sdk/PluginInterface'
import { Component, Vue, Prop, InjectReactive, Watch } from 'vue-property-decorator'

import ModuleHost from './ModuleHost.vue'

@Component({
  components: { ModuleHost }
})
export default class PluginHost extends Vue {
  @InjectReactive() readonly pluginRegistry!: PluginRegistryInterface
  @Prop() readonly uid!: string

  showComponent = false

  get plugins() {
    return this.pluginRegistry.plugins
  }

  myModules: PluginRegistrarInterface[] = []
  plugin: PluginRegistrarInterface | false = false

  loaded = false
  waiting = false
  dynamicComponent: any

  mounted() {
    // this.myModules = this.modules[this.uid]
    this.plugin = this.pluginRegistry.getByUid(this.uid)

    if (!this.plugin) {
      this.waiting = true
    } else {
      this.getPluginComponent()
    }
  }

  @Watch('plugins')
  onPluginsChanged() {
    if (this.waiting) {
      this.plugin = this.pluginRegistry.getByUid(this.uid)
      this.getPluginComponent()
    }
  }

  @Watch('$route')
  onRouteChanged() {
    this.plugin = this.pluginRegistry.getByUid(this.uid)
    this.getPluginComponent()
  }

  async getPluginComponent() {
    if (!this.plugin) {
      return
    }
    this.waiting = false
    this.showComponent = false

    const T = await (this.plugin as PluginRegistrarInterface).importer()
    this.dynamicComponent = Vue.extend(T.default)
    this.showComponent = true

    this.loaded = true
  }
}
</script>

<style>
</style>