<template>
  <div>
    <div>
      <h2>Plugin: Menu Manager Module</h2>
    </div>
    <div >
      
    </div>

    <v-list>
      <v-list-item v-for="(item, i) in items" :key="`sidebar_menu_item_ref_${i}`">
      <v-list-item-content>
        <v-list-item-title>{{ item.title }}</v-list-item-title>
      </v-list-item-content>
      <v-list-item-action>
        <v-btn icon @click="remove(item)"><v-icon>mdi-close</v-icon></v-btn>
      </v-list-item-action>
    </v-list-item>
    </v-list>

    <div>
      <v-text-field v-model="name" title="New Item Name" ></v-text-field>
    </div>

    <div>
      <v-btn @click="add">Add</v-btn>
    </div>
  </div>
</template>

<script lang="ts">
import { SidebarHookInterface, SideMenuItemInterface } from '@/core/interface/sdk/SidebarInterface'
import { Component, Inject, Prop, Vue } from 'vue-property-decorator'
import { SideMenuItem } from "@/core/components/side-menu/SideMenuModel"

@Component
export default class MenuManagerModule extends Vue {
  @Prop({ default: 'plugin-menu-manager' }) readonly uid!: string

  @Inject() readonly sidebarHook!: SidebarHookInterface

  get items(): SideMenuItemInterface[] {
    return this.sidebarHook.items
  }

  name = ''
  id = 0

  add() {
    this.id++
    this.sidebarHook.add(`${this.name} - ${this.id}`, new SideMenuItem(this.name))
    
    this.name = ''
  }
  
  remove(item: SideMenuItemInterface) {
    this.sidebarHook.remove(item)
  }
}
</script>

<style>
</style>