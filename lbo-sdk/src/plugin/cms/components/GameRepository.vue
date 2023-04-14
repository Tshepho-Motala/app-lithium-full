<template>
  <v-list data-test-id="lst-game-repository" v-if="shouldDisplay">
    <v-list-item v-for="(game, ii) in games" :key="`game-${ii}`">
      <v-list-item-avatar tile>
        <v-img v-if="game.cdnImageUrl" :alt="`${game.name} avatar`" :src="game.cdnImageUrl"></v-img>
        <v-avatar tile color="primary" v-else>
          <span class="white--text">{{ makeAcronym(game.name) }}</span>
        </v-avatar>
      </v-list-item-avatar>
      <v-list-item-content>
        <v-list-item-title v-text="game.name"></v-list-item-title>
      </v-list-item-content>
    </v-list-item>
  </v-list>
   <div v-else class="text-center pa-4">
    <span class="grey--text">Please select a lobby first</span>
  </div>
</template>

<script lang='ts'>
import { RootScopeInterface } from '@/core/interface/ScopeInterface'
import { Component, Inject, Prop, Vue, Watch } from 'vue-property-decorator'
import GameItemInterface from '../interfaces/GameItemInterface'
import LobbyItem from '../models/LobbyItem'

@Component
export default class GameRepository extends Vue {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Prop() lobby!: LobbyItem

  games: GameItemInterface[] = []
  loading = false

  get shouldDisplay(): boolean {
    return this.lobby !== null 
  }

  @Watch('shouldDisplay')
  onShouldDisplay(val: boolean) {
    if (val) {
      this.asyncMounted()
    } else {
      this.games = []
    }
  }

  makeAcronym(str: string): string {
    return str.match(/\b(\w)/g)!.join('')
  }

  async asyncMounted() {
    if (this.lobby.domain === null || this.lobby.page.channel) {
      return
    }

    try {
      this.loading = true

      this.games = await this.rootScope.provide.gamesProvider.getGamesByDomainAndEnabled(this.lobby.domain, true, true, this.lobby.page.channel)

      this.loading = false
    } catch (e) {
      console.error(e)
    }
  }
}
</script>
