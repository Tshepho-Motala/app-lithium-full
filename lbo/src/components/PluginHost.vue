<template>
  <div v-if="loaded">
    <component :is="dynamicComponent"></component>

    <ModuleHost
      v-for="(module, i) in myModules"
      :key="`${uid}-module-POSITION-${i}`"
      :module="module"
    />
  </div>
</template>

<script lang="ts">
import { PluginRegistrarInterface } from '@/common/interfaces'
import PluginManager from '@/plugins/PluginManager'
import { Component, Vue, Prop, Inject, Watch } from 'vue-property-decorator'

import ModuleHost from './ModuleHost.vue'

@Component({
  components: { ModuleHost }
})
export default class PluginHost extends Vue {
  @Inject() readonly pluginManager!: PluginManager
  @Prop() readonly uid!: string

  myModules: PluginRegistrarInterface[] = []
  plugin: PluginRegistrarInterface | false = false

  loaded = false
  waiting = false
  dynamicComponent: any

  mounted() {
    this.myModules = this.pluginManager.pageModules[this.uid]
    this.plugin = this.getPluginByUid()

    if (!this.plugin) {
      this.waiting = true
    } else {
      this.getPluginComponent()
    }
  }

    @Watch('pluginManager.pageModules')
    onModulesChanged() {
      this.myModules = this.pluginManager.pageModules[this.uid]
    }

  async getPluginComponent() {
    if (!this.plugin) {
      return
    }
    this.waiting = false
    this.dynamicComponent = await this.plugin.importer()
    
    this.loaded = true
  }

  getPluginByUid(): PluginRegistrarInterface | false {
    return this.pluginManager.getByUid(this.uid)
  }
}
</script>

<style>
</style>