<template>
  <v-row data-test-id="cnt-nav-code-mapper" v-if="shouldDisplay">
    <v-col cols="12" md="6">
      <v-text-field label="Primary Nav" v-model="lobby.page.primary_nav_code"></v-text-field>
    </v-col>
    <v-col cols="12" md="6">
      <v-text-field label="Secondary Nav" v-model="lobby.page.secondary_nav_code"></v-text-field>
    </v-col>
  </v-row>
  <div v-else class="text-center pa-4">
    <span class="grey--text">Please select a lobby first</span>
  </div>
</template>

<script lang='ts'>
import { Component, Prop, Vue } from 'vue-property-decorator'
import LobbyItem from '../models/LobbyItem'

@Component
export default class NavCodeMapper extends Vue {
  @Prop() lobby!: LobbyItem
  
  get shouldDisplay(): boolean {
    return this.lobby !== null
  }
}
</script>